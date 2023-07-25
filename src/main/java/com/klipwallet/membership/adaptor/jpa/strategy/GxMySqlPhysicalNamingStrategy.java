package com.klipwallet.membership.adaptor.jpa.strategy;

import java.io.Serializable;

import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * GroundX mysql 물리 DB 요소 명칭 전략.
 * <pre>
 *   TableName -> table_name
 *   columnName -> columnName
 * </pre>
 * 위 사항(테이블 명, 칼럼 명 전략) 이외에는 {@link PhysicalNamingStrategyStandardImpl} 전략을 승계한다.
 *
 * @see CamelCaseToUnderscoresNamingStrategy
 * @see PhysicalNamingStrategyStandardImpl
 */
public class GxMySqlPhysicalNamingStrategy implements PhysicalNamingStrategy, Serializable {
    private final CamelCaseToUnderscoresNamingStrategy camelCaseToUnderscoresNamingStrategy = new CamelCaseToUnderscoresNamingStrategy();

    @Override
    public Identifier toPhysicalCatalogName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
        return PhysicalNamingStrategyStandardImpl.INSTANCE.toPhysicalCatalogName(logicalName, jdbcEnvironment);
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
        return PhysicalNamingStrategyStandardImpl.INSTANCE.toPhysicalCatalogName(logicalName, jdbcEnvironment);
    }

    @Override
    public Identifier toPhysicalTableName(Identifier logicalName, JdbcEnvironment context) {
        return camelCaseToUnderscoresNamingStrategy.toPhysicalTableName(logicalName, context);
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier logicalName, JdbcEnvironment jdbcEnvironment) {
        return PhysicalNamingStrategyStandardImpl.INSTANCE.toPhysicalSequenceName(logicalName, jdbcEnvironment);
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier logicalName, JdbcEnvironment context) {
        return PhysicalNamingStrategyStandardImpl.INSTANCE.toPhysicalColumnName(logicalName, context);
    }
}
