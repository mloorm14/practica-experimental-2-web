# ADR-001: Selección de Tecnologías para el Backend

## Estado
Propuesto / En Revisión

## Contexto
El Proyecto Fin de Curso (PFC) requiere el desarrollo de un backend robusto, dinámico y seguro que implemente autenticación y módulos CRUD con sentencias preparadas. La guía exige evaluar e implementar la solución en al menos dos tecnologías de servidor en el entorno de desarrollo local (Windows 11).

## Decisión
Se eligió PHP 8.x con Apache/XAMPP como primera tecnología debido a su baja curva de aprendizaje, amplia disponibilidad en servicios de hosting compartido y soporte de funciones de seguridad nativas como password_hash, filter_input y PDO. Como segunda tecnología se seleccionó Java/JSP con Jakarta EE sobre Apache Tomcat 10 en lugar de ASP.NET Core, debido a la familiaridad del equipo con el ecosistema JVM. Además, ambas tecnologías comparten la misma base de datos MySQL, permitiendo comparar distintos enfoques de desarrollo backend. Jakarta EE también facilita la implementación de conceptos como Servlets, filtros @WebFilter y el patrón Repository mediante JDBC.

## Consecuencias
Como consecuencia positiva, la utilización de una base de datos MySQL compartida (appweb_unidad2) permite una interoperabilidad completa entre ambas implementaciones. PHP facilita un despliegue rápido mediante XAMPP, mientras que Java aporta tipado estático, mayor organización del código y filtros globales para el control de solicitudes.
Como consecuencia negativa, Java presenta una curva de aprendizaje más elevada debido al uso de Maven, Tomcat y al ciclo de vida de los Servlets. Asimismo, la configuración de archivos como context.xml y el despliegue mediante archivos .war resultan más complejos que una implementación basada únicamente en PHP.
