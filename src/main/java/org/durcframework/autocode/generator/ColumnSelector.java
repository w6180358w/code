package org.durcframework.autocode.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.durcframework.autocode.util.SqlHelper;

/**
 * 表信息查询
 */
public abstract class ColumnSelector {

	private DataBaseConfig dataBaseConfig;
	
	public ColumnSelector(DataBaseConfig dataBaseConfig){
		this.dataBaseConfig = dataBaseConfig;
	}

	/**
	 * 返回查询表字段信息的SQL语句,不同的数据查询表信息不一样
	 * 如mysql是DESC tableName
	 * @return
	 */
	protected abstract String getColumnInfoSQL(String tableName);
	
	/**
	 * 构建列信息
	 * @param rowMap
	 * @return
	 */
	protected abstract ColumnDefinition buildColumnDefinition(Map<String, Object> rowMap);
	
	public void setColumnDefinitions(TableDefinition tableDefinition) {
		List<Map<String, Object>> resultList = SqlHelper.runSql(this.getDataBaseConfig(), getColumnInfoSQL(tableDefinition.getTableName()));
		
		List<ColumnDefinition> columnDefinitionList = new ArrayList<ColumnDefinition>(resultList.size());
		List<ColumnDefinition> allColumnDefinitionList = new ArrayList<ColumnDefinition>(resultList.size());
		String ignore = this.getDataBaseConfig().getIgnore();
		List<String> ignores = new ArrayList<>();
		if(StringUtils.isNotEmpty(ignore)) {
			for (String ig : ignore.split(",")) {
				ignores.add(ig.trim().toUpperCase());
			}
		}
		// 构建columnDefinition
		for (Map<String, Object> rowMap : resultList) {
			ColumnDefinition col = buildColumnDefinition(rowMap);
			allColumnDefinitionList.add(col);
			if(StringUtils.isNotEmpty(ignore)) {
				if(!ignores.contains(col.getColumnNameUp().trim().toUpperCase())) {
					columnDefinitionList.add(col);
				}
			}else {
				columnDefinitionList.add(col);
			}
		}
		tableDefinition.setAllColumnDefinitions(allColumnDefinitionList);
		tableDefinition.setColumnDefinitions(columnDefinitionList);
		afterColumnDefinitions(tableDefinition.getTableName(), columnDefinitionList);
	}

	protected void afterColumnDefinitions(String tableName,List<ColumnDefinition> columnDefinitionList){
		
	}
	
	public DataBaseConfig getDataBaseConfig() {
		return dataBaseConfig;
	}

	public void setDataBaseConfig(DataBaseConfig dataBaseConfig) {
		this.dataBaseConfig = dataBaseConfig;
	}

}
