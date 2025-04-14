package com.hotel.webapp.config;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.jpa.boot.spi.IntegratorProvider;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Value;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomSchemaIntegrator implements IntegratorProvider {

  @Override
  public List<Integrator> getIntegrators() {
    return Collections.singletonList(new CustomIntegrator());
  }

  private static class CustomIntegrator implements Integrator {

    @Override
    public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
      Database database = metadata.getDatabase();
      for (Namespace namespace : database.getNamespaces()) {
        for (org.hibernate.mapping.Table table : namespace.getTables()) {
          // Find the PersistentClass associated with this table
          PersistentClass persistentClass = findPersistentClassForTable(metadata, table.getName());
          if (persistentClass != null) {
            List<Column> orderedColumns = getOrderedColumns(persistentClass);

            // Replace the table's columns with the reordered ones
            table.getColumns().clear();
            for (Column column : orderedColumns) {
              table.addColumn(column);
            }
          }
        }
      }
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
      // No cleanup needed
    }

    // Find the PersistentClass that maps to the given table name
    private PersistentClass findPersistentClassForTable(Metadata metadata, String tableName) {
      for (PersistentClass persistentClass : metadata.getEntityBindings()) {
        if (tableName.equals(persistentClass.getTable().getName())) {
          return persistentClass;
        }
      }
      return null; // Table not mapped to an entity
    }

    // Retrieve the actual Column objects in the order of the entity's field declarations
    private List<Column> getOrderedColumns(PersistentClass persistentClass) {
      List<Column> orderedColumns = new ArrayList<>();
      try {
        Class<?> entityClass = Class.forName(persistentClass.getClassName());
        Field[] fields = entityClass.getDeclaredFields();

        for (Field field : fields) {
          try {
            Property property = persistentClass.getProperty(field.getName());
            if (property != null) {
              Value value = property.getValue();
              if (value instanceof SimpleValue) {
                SimpleValue simpleValue = (SimpleValue) value;
                orderedColumns.addAll(simpleValue.getColumns());
              } else {
                // Skip complex mappings (e.g., collections) for now
                System.err.println("Skipping complex value for property: " + field.getName() + " in " + entityClass.getName());
              }
            }
          } catch (org.hibernate.MappingException e) {
            // Skip unmapped fields (e.g., transient)
          }
        }
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
      return orderedColumns;
    }
  }
}