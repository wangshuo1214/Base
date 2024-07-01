package com.ws.base.service;

import com.ws.base.model.DictData;
import com.ws.base.model.DictType;

import java.util.List;

public interface IDictService {

    int addDictType(DictType dictType);

    List<DictType> queryDictType(DictType dictType);

    int updateDictType(DictType dictType);

    DictType getDictType(String id);

    int deleteDictType(List<String> ids);

    int addDictData(DictData dictData);

    int updateDictData(DictData dictData);

    DictData getDictData(String id);

    List<DictData> queryDictData(DictData dictData);

    int deleteDictData(List<String> ids);

    List<DictData> getDictDataByType(String dictType);

    DictData getSoleDict(String dictType, String dictCode);
}
