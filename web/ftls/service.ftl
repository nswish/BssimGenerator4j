package com.baosight.bssim.${firstModule?lower_case}<#if secondModule?has_content>.${secondModule?lower_case}</#if>.service;

import com.baosight.bssim.common.exception.BssimException;
import ${table.package}.${table.className};
import com.baosight.iplat4j.core.ei.EiBlock;
import com.baosight.iplat4j.core.ei.EiConstant;
import com.baosight.iplat4j.core.ei.EiInfo;
import com.baosight.iplat4j.ep.ServiceEPBase;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class ${serviceName} extends ServiceEPBase {
    public EiInfo initLoad(EiInfo inInfo) {
        inInfo.addBlock("save");
        inInfo.addBlock("result");
        return _query(inInfo);
    }

    public EiInfo query(EiInfo inInfo) {
        try {
            return _query(inInfo);
        } catch (Exception ex) {
            inInfo.setStatus(-1);
            inInfo.setMsg(ex.getMessage());
            return inInfo;
        }
    }

    public EiInfo save(EiInfo inInfo) {
        try{
            String id = inInfo.getCellStr("save", 0, ${table.className}.fields.id);

            if(StringUtils.isBlank(id)){
                // 新增记录
                ${table.className}.newInstance(inInfo.getRow("save", 0)).save();
            } else {
                // 修改记录
                ${table.className} obj = ${table.className}.find(id);

                if (obj != null) {
                    obj.fromMap(inInfo.getRow("save", 0));
                    obj.save();
                } else {
                    throw new BssimException("目标数据不存在！");
                }
            }

            inInfo.setMsg("已保存！");

            return _query(inInfo);
        } catch (Exception ex) {
            inInfo.setStatus(-1);
            inInfo.setMsg(ex.getMessage());
            return _query(inInfo);
        }
    }

    public EiInfo delete(EiInfo inInfo) {
        try {
            EiBlock result = inInfo.getBlock("result");

            for(int i=0; i<result.getRowCount(); i++) {
                String id = result.getCellStr(i, ${table.className}.fields.id);
                ${table.className} obj = ${table.className}.find(id);

                if (obj != null) {
                    obj.delete();
                }
            }

            inInfo.setMsg("已删除！");

            return _query(inInfo);
        } catch (Exception ex) {
            inInfo.setStatus(-1);
            inInfo.setMsg(ex.getMessage());
            return _query(inInfo);
        }
    }

    private EiInfo _query(EiInfo inInfo) {
        List result = ${table.className}.where(inInfo.getRow(EiConstant.queryBlock, 0)).limit(inInfo.addBlock("result")).all();
        inInfo.addBlock("result").setBlockMeta(new ${table.className}().eiMetadata);
        inInfo.getBlock("result").setRows(result);
        return inInfo;
    }
}