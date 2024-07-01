package com.ws.base.mapper;

import com.ws.base.model.DictData;
import com.ws.base.model.DictType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DictMapper {

    int addDictType(DictType dictType);

    List<DictType> queryDictType(DictType dictType);

    List<DictType> queryDictByDictType(String dictType);

    int updateDictType(DictType dictType);

    DictType getDictType(String id);

    int deleteDictType(List<String> ids);

    int deleteDictDataByType(String id);

    List<DictData> checkDictDataUnique(DictData dictData);

    int addDictData(DictData dictData);

    int updateDictData(DictData dictData);

    DictData getDictData(String id);

    List<DictData> queryDictData(DictData dictData);

    int deleteDictData(List<String> ids);

    List<DictData> getDictDataByType(String dictType);

    DictData getSoleDict(@Param("dictType") String dictType, @Param("dictCode") String dictCode);
}
