package cn.com.mfish.common.code.common;

import cn.com.mfish.common.code.config.properties.FreemarkerProperties;
import cn.com.mfish.common.code.entity.CodeInfo;
import cn.com.mfish.common.code.entity.FieldExpand;
import cn.com.mfish.common.code.entity.SearchInfo;
import cn.com.mfish.common.code.entity.TableExpand;
import cn.com.mfish.common.code.req.ReqCode;
import cn.com.mfish.common.code.req.ReqSearch;
import cn.com.mfish.common.code.vo.CodeVo;
import cn.com.mfish.common.core.constants.RPCConstants;
import cn.com.mfish.common.core.entity.DefaultField;
import cn.com.mfish.common.core.exception.MyRuntimeException;
import cn.com.mfish.common.core.utils.StringUtils;
import cn.com.mfish.common.core.web.PageResult;
import cn.com.mfish.common.core.web.ReqPage;
import cn.com.mfish.common.core.web.Result;
import cn.com.mfish.sys.api.entity.FieldInfo;
import cn.com.mfish.sys.api.entity.TableInfo;
import cn.com.mfish.sys.api.remote.RemoteDbConnectService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.annotation.Resource;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author: mfish
 * @description: 代码生成工具类
 * @date: 2022/8/24 15:30
 */
@Component
@Slf4j
@RefreshScope
public class FreemarkerUtils {

    @Resource
    Configuration fmConfig;
    @Resource
    FreemarkerProperties freemarkerProperties;
    @Resource
    RemoteDbConnectService remoteDbConnectService;
    @Value("${code.savePath.front}")
    String frontPath;
    @Value("${code.savePath.back}")
    String backPath;

    /**
     * 初始化请求参数
     *
     * @param reqCode 请求参数
     */
    @SuppressWarnings("all") // 缓解强迫症
    public void initReqCode(ReqCode reqCode) {
        // 校验数据库连接ID
        if (StringUtils.isEmpty(reqCode.getConnectId())) {
            throw new MyRuntimeException("错误:库名不允许为空");
        }

        // 校验表名
        if (StringUtils.isEmpty(reqCode.getTableName())) {
            throw new MyRuntimeException("错误:表名不允许为空");
        }

        // 校验包名
        // 包名为空时默认为cn.com.mfish.web
        if (StringUtils.isEmpty(reqCode.getPackageName())) {
            reqCode.setPackageName("cn.com.mfish.web");
        }

        // 包名不为空时，校验包名格式
        else if (!StringUtils.isMatch("^([A-Za-z]{1}[A-Za-z\\d_]*\\.?)+[A-Za-z][A-Za-z\\d_]*$", reqCode.getPackageName())) {
            throw new MyRuntimeException("错误:包名格式不规范");
        }

        // 校验接口前缀
        if (StringUtils.isEmpty(reqCode.getApiPrefix())) {
            // 如果apiPrefix为空，则尝试从packageName中提取apiPrefix
            int index = reqCode.getPackageName().lastIndexOf(".");
            if (index >= 0) {
                // 如果包名中包含点，则提取点之后的部分作为apiPrefix
                reqCode.setApiPrefix(reqCode.getPackageName().substring(index + 1));
            } else {
                // 如果包名中不包含点，则直接使用包名作为apiPrefix
                reqCode.setApiPrefix(reqCode.getPackageName());
            }
        }

        // 如果apiPrefix不为空，则检查apiPrefix的格式是否符合规范
        // 规范要求apiPrefix以字母开头，后面可以跟字母、数字或下划线
        else if (!StringUtils.isMatch("^[A-Za-z]+[A-Za-z\\d_]*$", reqCode.getApiPrefix())) {
            throw new MyRuntimeException("错误:接口前缀格式不规范");
        } else {
            // 如果格式符合规范，则将apiPrefix转为小写
            reqCode.setApiPrefix(reqCode.getApiPrefix().toLowerCase());
        }

        // 校验实体名
        if (StringUtils.isEmpty(reqCode.getEntityName())) {
            // 如果entityName为空，则使用表名作为entityName
            reqCode.setEntityName(reqCode.getTableName());
        } else if (!StringUtils.isMatch("^[A-Za-z]+[A-Za-z\\d_]*$", reqCode.getEntityName())) {
            throw new MyRuntimeException("错误:实体类名格式不规范");
        }
        // 如果entityName带下划线，转为首字母大写驼峰，否则其他不变首字母大写
        if (reqCode.getEntityName().contains("_")) {
            reqCode.setEntityName(StringUtils.toCamelBigCase(reqCode.getEntityName()));
        } else {
            reqCode.setEntityName(StringUtils.firstUpperCase(reqCode.getEntityName()));
        }

        // 检查表注释是否为空
        if (StringUtils.isEmpty(reqCode.getTableComment())) {
            // 如果tableComment为空，则将表名作为表描述
            reqCode.setTableComment(reqCode.getTableName());

            // 从远程数据库服务获取表列表
            Result<PageResult<TableInfo>> result =
                remoteDbConnectService.getTableList(RPCConstants.INNER, reqCode.getConnectId(), reqCode.getTableName(),
                    new ReqPage().setPageNum(1).setPageSize(10000));

            // 如果结果不成功，抛出自定义异常
            if (!result.isSuccess()) {
                throw new MyRuntimeException(result.getMsg());
            }

            // 如果结果数据为空或列表为空，抛出自定义异常
            if (result.getData() == null || result.getData().getList() == null || result.getData().getList().isEmpty()) {
                throw new MyRuntimeException("错误:未获取到表信息");
            }

            // 获取表信息
            TableInfo tableInfo = result.getData().getList().get(0);

            // 如果表信息不为空且表注释不为空，则更新表注释
            if (tableInfo != null && StringUtils.isNotEmpty(tableInfo.getTableComment())) {
                reqCode.setTableComment(tableInfo.getTableComment());
            }
        }
    }

    /**
     * 获取代码
     *
     * @param reqCode 请求参数
     * @return 代码列表
     */
    public List<CodeVo> getCode(ReqCode reqCode) {
        // 初始化请求参数
        initReqCode(reqCode);

        // 设置代码信息的包名、实体名和接口前缀
        CodeInfo codeInfo = new CodeInfo();
        codeInfo.setPackageName(reqCode.getPackageName());
        codeInfo.setEntityName(reqCode.getEntityName());
        codeInfo.setApiPrefix(reqCode.getApiPrefix());

        // 获取字段信息
        Result<PageResult<FieldInfo>> result =
            remoteDbConnectService.getFieldList(RPCConstants.INNER, reqCode.getConnectId(), reqCode.getTableName(),
                new ReqPage().setPageNum(1).setPageSize(10000));
        if (!result.isSuccess()) {
            throw new MyRuntimeException(result.getMsg());
        }
        if (result.getData() == null || result.getData().getList() == null || result.getData().getList().isEmpty()) {
            throw new MyRuntimeException("错误:未获取到字段信息");
        }
        List<FieldInfo> list = result.getData().getList();

        // 构建搜索字段信息
        codeInfo.setSearchList(buildSearch(reqCode.getSearches(), list));

        // 初始化ID类型和扩展字段列表
        String idType = "";
        List<FieldExpand> expandList = new ArrayList<>();

        // 过滤掉默认字段，这些字段不需要生成
        for (int i = 0; i < list.size(); i++) {
            FieldInfo fieldInfo = list.get(i);
            String fieldName = fieldInfo.getFieldName();
            if (StringUtils.isEmpty(fieldName)) {
                continue;
            }
            fieldName = fieldName.toLowerCase(Locale.ROOT);
            if (DefaultField.values.contains(fieldName)) {
                // 设置唯一ID类型
                if ("id".equals(fieldName)) {
                    idType = fieldInfo.getType();
                }
                list.remove(i--);
                continue;
            }
            final String finalFieldName = fieldName;
            Optional<SearchInfo> searchInfo = codeInfo.getSearchList().stream().filter((search) ->
                            search.getFieldInfo().getFieldName().equals(finalFieldName) && search.getComponent() != null && search.getComponent().size() == 2)
                    .findFirst();
            fieldInfo.setFieldName(StringUtils.toCamelCase(fieldName));
            FieldExpand fieldExpand = new FieldExpand().setFieldInfo(fieldInfo);
            // 设置字典组件列表
            searchInfo.ifPresent(info -> fieldExpand.setDictComponent(info.getComponent()));
            expandList.add(fieldExpand);
        }

        // 构建表扩展信息
        TableExpand tableExpand = new TableExpand();
        tableExpand.setColumns(list).setTableName(reqCode.getTableName())
            .setTableComment(reqCode.getTableComment()).setIdType(idType);
        codeInfo.setTableInfo(tableExpand.setFieldExpands(expandList));

        // 获取最终的代码信息列表
        return getCode(codeInfo);
    }

    private List<SearchInfo> buildSearch(List<ReqSearch> list, List<FieldInfo> fields) {
        List<SearchInfo> searchInfos = new ArrayList<>();
        if (list == null || list.isEmpty()) {
            return searchInfos;
        }
        for (ReqSearch reqSearch : list) {
            Optional<FieldInfo> optional = fields.stream().filter((field) -> field.getFieldName().equals(reqSearch.getField())).findFirst();
            if (Optional.empty().equals(optional)) {
                continue;
            }
            SearchInfo searchInfo = new SearchInfo();
            searchInfo.setFieldInfo(optional.get())
                .setField(StringUtils.toCamelBigCase(reqSearch.getField()))
                .setCondition(reqSearch.getCondition())
                .setComponent(reqSearch.getComponent());
            searchInfos.add(searchInfo);
        }
        return searchInfos;
    }

    /**
     * 获取生成的代码并返回前端
     *
     * @param codeInfo 代码参数信息
     * @return 代码列表
     */
    public List<CodeVo> getCode(CodeInfo codeInfo) {
        // 代码列表
        List<CodeVo> list = new ArrayList<>();
        // 构建正则替换Map
        Map<String, String> regexMap = buildRegexMap(codeInfo);
        // 遍历模板文件
        for (String key : freemarkerProperties.getKeys()) {
            // 代码相关信息对象
            CodeVo codeVo = new CodeVo();
            // 替换变量
            String path = replaceVariable(key, regexMap);
            int index = path.lastIndexOf("/");
            String tempName = path.substring(index + 1).replace(".ftl", "");
            codeVo.setName(tempName).setPath(path.substring(0, index));
//            if (key.contains("xml")) {
//                //xml需要转义后返回
//                codeVo.setCode(StringUtil.XMLEnc(buildCode(key, codeInfo)));
//                continue;
//            }
            codeVo.setCode(buildCode(key, codeInfo));
            list.add(codeVo);
        }
        return list;
    }

    /**
     * 构建正则替换Map
     *
     * @param codeInfo
     * @return
     */
    private Map<String, String> buildRegexMap(CodeInfo codeInfo) {
        Map<String, String> map = new HashMap<>();
        map.put("\\$\\{entityName\\#?(?<param>.*?)}", codeInfo.getEntityName());
        map.put("\\$\\{apiPrefix\\#?(?<param>.*?)}", codeInfo.getApiPrefix());
        String path = codeInfo.getPackageName().replace(".", "/");
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        map.put("\\$\\{packageName\\#?(?<param>.*?)}", path);
        return map;
    }

    /**
     * 参数替换
     *
     * @param key
     * @return
     */
    private String replaceVariable(String key, Map<String, String> mapValue) {
        for (Map.Entry<String, String> kv : mapValue.entrySet()) {
            Pattern pattern = Pattern.compile(kv.getKey());
            Matcher matcher = pattern.matcher(key);
            while (matcher.find()) {
                String value = matcher.group();
                if (StringUtils.isEmpty(value)) {
                    continue;
                }
                int index = value.indexOf("#");
                if (index <= 0) {
                    key = key.replace(value, MatchType.不处理.dealVariable(kv.getValue()));
                    continue;
                }
                key = key.replace(value, MatchType.getMatchType(value.substring(index + 1).replace("}", "")).dealVariable(kv.getValue()));
            }
        }
        return key;
    }

    /**
     * 模版代码构建
     *
     * @param template 模板
     * @param codeInfo 代码信息
     * @return 代码
     */
    public String buildCode(String template, CodeInfo codeInfo) {
        // 创建一个StringWriter对象，用于输出生成的代码
        StringWriter stringWriter = new StringWriter();
        // 使用try-with-resources语句，确保Writer对象在代码执行完毕后被关闭
        try (Writer out = new BufferedWriter(stringWriter)) {
            // 获取模板
            Template temp = fmConfig.getTemplate(template, "utf-8");
            // 使用模板生成代码
            temp.process(codeInfo, out);
            // 刷新输出流
            out.flush();
        } catch (IOException | TemplateException e) {
            log.error("生成代码异常:" + e.getMessage(), e);
            throw new MyRuntimeException(e);
        }
        // 返回生成的代码
        return stringWriter.toString();
    }

    /**
     * 保存代码
     *
     * @param reqCode 代码参数
     * @return
     */
    public boolean saveCode(ReqCode reqCode) {
        List<CodeVo> list = getCode(reqCode);
        List<CodeVo> listBack = list.stream().filter((codeVo -> codeVo.getPath().startsWith("src/main/java"))).toList();
        List<CodeVo> listFront = list.stream().filter((codeVo -> codeVo.getPath().startsWith("mfish-nocode-view"))).toList();
        return saveCode(listBack, backPath) && saveCode(listFront, frontPath);

    }

    /**
     * 保存代码到本地
     *
     * @param list
     * @return
     */
    public boolean saveCode(List<CodeVo> list, String savePath) {
        savePath = savePath.replace("\\", "/");
        if (StringUtils.isEmpty(savePath)) {
            savePath = "/mfish/";
        } else if (!savePath.endsWith("/")) {
            savePath += "/";
        }
        for (CodeVo code : list) {
            try {
                File file = new File(savePath + "/" + code.getPath(), code.getName());
                FileUtils.writeStringToFile(file, code.getCode(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                log.error("错误:文件保存异常", e);
            }
        }
        // 压缩样例，暂不压缩
        // ZipUtils.toZip(path, true);
        return true;
    }

    /**
     * 下载代码
     *
     * @param reqCode 请求参数
     * @return
     */
    public byte[] downloadCode(ReqCode reqCode) {
        List<CodeVo> list = getCode(reqCode);
        ZipOutputStream zos;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            zos = new ZipOutputStream(outputStream);
            for (CodeVo codeVo : list) {
                zos.putNextEntry(new ZipEntry(codeVo.getPath() + "/" + codeVo.getName()));
                IOUtils.write(codeVo.getCode(), zos, StandardCharsets.UTF_8);
                zos.flush();
                zos.closeEntry();
            }
            // 需要主动关闭后，再返回outputStream否则返回文件不全
            IOUtils.closeQuietly(zos);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new MyRuntimeException("错误:下载代码失败");
        }
    }
}
