# ADR-002: Estrategia de Persistencia y Base de Datos

## Estado
Propuesto / En Revisión

## Contexto
Se necesita una arquitectura de datos compartida que permita a la aplicación web en PHP y a la aplicación en Java/JSP consultar, insertar, actualizar y eliminar registros de usuarios y productos de forma centralizada e interoperable, mitigando ataques de Inyección SQL.

## Decisión
Se eligió MySQL 8.x como motor de base de datos único compartido entre ambas aplicaciones, con motor InnoDB para habilitar claves foráneas y restricciones relacionales. El acceso desde PHP se realiza vía PDO con ATTR_EMULATE_PREPARES => false; desde Java vía JDBC con PreparedStatement. El esquema vive en database/schema.sql en el repositorio.

## Consecuencias
Pro — consistencia total de datos mediante FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE; sentencias preparadas reales en ambas tecnologías eliminan SQLi (CVSS 10.0); un solo esquema para mantener. 
Contra — dependencia de un único servidor MySQL local (XAMPP); en producción requeriría replicación o migración a un servicio administrado.
