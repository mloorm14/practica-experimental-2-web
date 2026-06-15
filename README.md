# GA — Práctica Experimental Unidad II: Arquitectura Backend Multi-Tecnología

Este repositorio (Monorepo) contiene el desarrollo correspondiente a la Práctica Experimental de la Unidad II de la asignatura Aplicaciones Web (5to Nivel - SOFT-R-A). El sistema implementa un backend robusto e interoperable para la gestión de usuarios y catálogo de productos, ejecutándose simultáneamente sobre dos tecnologías de servidor que comparten una misma capa de persistencia MySQL.

## 📁 Estructura del Repositorio

```text
/
├── docs/                       # Documentación arquitectónica obligatoria
│   └── adr/                    # Architecture Decision Records (ADRs)
│       ├── ADR-001-tecnologia-backend.md
│       └── ADR-002-estrategia-bd.md
├── unidad2_php/                # PROYECTO COMPLETO EN PHP 8.x
│   ├── config/                 # Conexión PDO en modo singleton
│   ├── includes/               # Componentes de seguridad (auth, csrf, headers)
│   ├── repositories/           # Implementación de Patrón Repository
│   └── *.php                   # Vistas y controladores del flujo
├── unidad2_java/               # PROYECTO COMPLETO EN JAVA/JSP (Jakarta EE 10)
│   ├── pom.xml                 # Descriptor de dependencias Maven (Tomcat 10, BCrypt)
│   └── src/main/               # Código fuente estructurado en Servlets, Filtros y JSTL
└── README.md                   # Documentación principal del repositorio
```

## 🛠️ Especificaciones Técnicas e Interoperabilidad

- **Base de Datos Compartida:** Ambas aplicaciones interactúan con el motor MySQL (v8.x/XAMPP) a través del esquema `appweb_unidad2`, utilizando el motor **InnoDB** para garantizar integridad referencial (ON DELETE CASCADE).
- **Backend PHP 8.x:** Refactorizado para utilizar el **Patrón Repository**, separando la lógica de negocio de la infraestructura de datos. Implementa sentencias preparadas nativas (`PDO::ATTR_EMULATE_PREPARES = false`).
- **Backend Java/JSP:** Estructura modular sobre Tomcat 10, utilizando Servlets para el control de flujo y JSP/JSTL () para la presentación server-side.

## 🛡️ Mitigaciones de Seguridad OWASP Implementadas

1. **Inyección SQL (A03):** Uso estricto de Prepared Statements parametrizados en PDO y JDBC.
2. **Cross-Site Scripting (XSS):** Saneamiento de toda salida dinámica mediante `htmlspecialchars(ENT_QUOTES | ENT_SUBSTITUTE)` en PHP y  en Java/JSP.
3. **Fallas de Autenticación (A07):** Hashing criptográfico irreversible utilizando el algoritmo **BCrypt** (`password_hash` en PHP y `jBCrypt` en Java) con un factor de coste de 12.
4. **Fijación de Sesión (A01):** Regeneración forzada del ID de sesión (`session_regenerate_id(true)`) post-autenticación.
5. **Cross-Site Request Forgery (CSRF):** Inyección de tokens de alta entropía en formularios críticos, validados mediante comparaciones de tiempo constante (`hash_equals`).
6. **Configuración Incorrecta (A05):** Inyección mediante interceptores/filtros globales de cabeceras de seguridad HTTP (`X-Frame-Options`, `X-Content-Type-Options`, `Content-Security-Policy`).

*Desarrollado bajo los lineamientos y buenas prácticas de Aplicaciones Web — UTEQ 2026*
