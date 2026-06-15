# ADR-002: Estrategia de Persistencia y Base de Datos

## Estado
Propuesto / En Revisión

## Contexto
Se necesita una arquitectura de datos compartida que permita a la aplicación web en PHP y a la aplicación en Java/JSP consultar, insertar, actualizar y eliminar registros de usuarios y productos de forma centralizada e interoperable, mitigando ataques de Inyección SQL.

## Decisión
[INTEGRANTE: Aquí debes redactar la decisión de utilizar el motor relacional MySQL a través de XAMPP, detallando el uso del motor InnoDB para habilitar claves foráneas y restricciones relacionales].

## Consecuencias
[INTEGRANTE: Redactar las consecuencias. Pros: Consistencia total de los datos mediante restricciones de integridad referencial (FOREIGN KEY), pool de conexiones nativo, soporte completo de Prepared Statements (PDO y JDBC). Contras: Dependencia de un único punto de fallo en el servidor local de base de datos].