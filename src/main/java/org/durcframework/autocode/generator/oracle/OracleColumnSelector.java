package org.durcframework.autocode.generator.oracle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.durcframework.autocode.generator.ColumnDefinition;
import org.durcframework.autocode.generator.ColumnSelector;
import org.durcframework.autocode.generator.DataBaseConfig;
import org.durcframework.autocode.util.SqlHelper;
import org.springframework.util.StringUtils;

/**
 * mysql表信息查询
 *
 */
public class OracleColumnSelector extends ColumnSelector {
	
	public OracleColumnSelector(DataBaseConfig dataBaseConfig) {
		super(dataBaseConfig);
	}

	private static final String pkSql = "SELECT" + 
			"	COL.COLUMN_NAME " + 
			"FROM" + 
			"	USER_CONSTRAINTS CON," + 
			"	USER_CONS_COLUMNS COL " + 
			"WHERE" + 
			"	CON.CONSTRAINT_NAME = COL.CONSTRAINT_NAME " + 
			"	AND CON.CONSTRAINT_TYPE = 'P' " + 
			"	AND COL.TABLE_NAME = '%s'";
	/**
	 * SHOW FULL COLUMNS FROM 表名
	 */
	@Override
	protected String getColumnInfoSQL(String tableName) {
		return "SELECT" + 
				"	COL.TABLE_NAME, " + 
				"	COL.COLUMN_NAME, " + 
				"	COL.DATA_TYPE, " + 
				"	COL.DATA_LENGTH, " + 
				"	COL.NULLABLE, " + 
				"	COM.COMMENTS " + 
				"FROM" + 
				"	USER_TAB_COLS COL " + 
				"	,USER_COL_COMMENTS COM " + 
				"WHERE" + 
				"	COL.TABLE_NAME = COM.TABLE_NAME " + 
				"	AND " + 
				"	COL.COLUMN_NAME = COM.COLUMN_NAME " + 
				"	AND " + 
				"	COL.TABLE_NAME = '" + tableName + "'";
	}
	
	@Override
	public void afterColumnDefinitions(String tableName,List<ColumnDefinition> columnDefinitionList) {
		//主键列
		List<Map<String, Object>> pkList = SqlHelper.runSql(this.getDataBaseConfig(), String.format(pkSql, tableName));
		Map<String,String> pkMap = new HashMap<String, String>(5);
		for (Map<String, Object> pk : pkList) {
			pkMap.put(pk.get("COLUMN_NAME").toString(), pk.get("COLUMN_NAME").toString());
		}
		// 构建columnDefinition
		for (ColumnDefinition col : columnDefinitionList) {
			if(pkMap.get(col.getColumnName())!=null) {
				col.setIsPk(true);
			}
		}
	}
	
	/*
	 * {FIELD=username, EXTRA=, COMMENT=用户名, COLLATION=utf8_general_ci, PRIVILEGES=select,insert,update,references, KEY=PRI, NULL=NO, DEFAULT=null, TYPE=varchar(20)}
	 */
	protected ColumnDefinition buildColumnDefinition(Map<String, Object> rowMap){
		Set<String> columnSet = rowMap.keySet();
		
		for (String columnInfo : columnSet) {
			rowMap.put(columnInfo.toUpperCase(), rowMap.get(columnInfo));
		}
		
		ColumnDefinition columnDefinition = new ColumnDefinition();
		
		columnDefinition.setColumnName((String)rowMap.get("COLUMN_NAME"));
		
		String type = (String)rowMap.get("DATA_TYPE");
		columnDefinition.setType(buildType(type));
		
		columnDefinition.setComment((String)rowMap.get("COMMENTS"));
		
		try {
			columnDefinition.setSize(Long.parseLong((rowMap.get("DATA_LENGTH")+"")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		columnDefinition.setNotNull("N".equals(rowMap.get("NULLABLE")));
		
		return columnDefinition;
	}
	
	// 将varchar(50)转换成VARCHAR
	private String buildType(String type){
		//Oracle中布尔类型为NUMBER(1)
		if("NUMBER(1)".equals(type)) {
			return "BIT";
		}
		if (StringUtils.hasText(type)) {
			int index = type.indexOf("(");
			if (index > 0) {
				return type.substring(0, index).toUpperCase();
			}
			return type;
		}
		return "VARCHAR";
	}
	
}
