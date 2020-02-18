package org.durcframework.autocode.generator.oracle;

import java.util.Map;

import org.durcframework.autocode.generator.ColumnSelector;
import org.durcframework.autocode.generator.DataBaseConfig;
import org.durcframework.autocode.generator.TableDefinition;
import org.durcframework.autocode.generator.TableSelector;

/**
 * 查询mysql数据库表
 */
public class OracleTableSelector extends TableSelector {

	public OracleTableSelector(ColumnSelector columnSelector,
			DataBaseConfig dataBaseConfig) {
		super(columnSelector, dataBaseConfig);
	}

	@Override
	protected String getShowTablesSQL(String dbName) {
		String sql = "SELECT * FROM USER_TAB_COMMENTS ";
		if(this.getSchTableNames() != null && this.getSchTableNames().size() > 0) {
			StringBuilder tables = new StringBuilder();
			for (String table : this.getSchTableNames()) {
				tables.append(",'").append(table).append("'");
			}
			sql += " WHERE TABLE_NAME IN (" + tables.substring(1) + ")";
		}
		return sql;
	}

	@Override
	protected TableDefinition buildTableDefinition(Map<String, Object> tableMap) {
		TableDefinition tableDefinition = new TableDefinition();
		tableDefinition.setTableName((String)tableMap.get("TABLE_NAME"));
		tableDefinition.setComment((String)tableMap.get("COMMENTS"));
		return tableDefinition;
	}

}
