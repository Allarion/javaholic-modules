# Persistence Conventions

> Ziel: Persistenz **ohne eigenes Modul**, **ohne Service-Fassaden**,
> **ohne „Persistence-API“**, ausschließlich über **klare, erzwingbare Konventionen**.

Persistenz ist **kein Feature**, sondern Infrastruktur.
Sie wird nicht konsumiert, sondern **implizit genutzt**.

---

## 1. Grundprinzip

Persistenz ist eine **Implementierungsdetail-Schicht**.

- ❌ kein eigenes Maven-Modul
- ❌ keine generischen Persistence-Interfaces
- ❌ keine „Repository Services“
- ❌ keine Magie

✔ klare Struktur  
✔ eindeutige Namensregeln  
✔ Spring Auto-Wiring  
✔ Austauschbarkeit durch Package-Swap  

---

## 2. Schichtenmodell

```
domain/
  user/
    User
    UserId
    Role
    Permission

persistence/
  jpa/
    user/
      JpaUser
      JpaRole
      JpaPermission
      JpaUserRepository
      UserMapper
```

**Regel:**
- `domain/**` kennt **keine** Persistenz
- `persistence/**` kennt **domain**, aber nicht umgekehrt

---

## 3. Domain-Regeln

### 3.1 Entities & Aggregates

- Domain-Objekte sind **reines Modell**
- Keine JPA-Annotationen
- Keine technischen Abhängigkeiten

```java
public class User {
    private UserId id;
    private String username;
    private Set<Role> roles;
}
```

---

### 3.2 IDs

- IDs sind **Value Objects**
- Keine primitiven IDs (`Long`, `UUID`) im Domain-Modell

```java
public record UserId(UUID value) {}
```

---

## 4. Persistence-Regeln (JPA)

### 4.1 Entity-Spiegelung

- JPA-Entities spiegeln Domain-Objekte **1:1**
- Naming: `Jpa<DomainType>`

```java
@Entity
@Table(name = "users")
public class JpaUser {
    @EmbeddedId
    private JpaUserId id;

    private String username;
}
```

---

### 4.2 Repositories

- Pro Aggregate **ein Repository**
- Repository arbeitet **nur mit JPA-Entities**

```java
public interface JpaUserRepository
        extends JpaRepository<JpaUser, JpaUserId> {
}
```

---

## 5. Mapping

### 5.1 Ein Mapper pro Aggregate

- **keine generischen Mapper**
- klar benannt
- lokal auffindbar

```java
@Mapper(componentModel = "spring")
public interface UserMapper {

    User toDomain(JpaUser entity);

    JpaUser toEntity(User domain);
}
```

---

### 5.2 Mapping-Regeln

- Domain ↔ Persistence ist **explizit**
- Keine implizite Feldmagie
- Anpassungen sind lokal sichtbar

---

## 6. Stores statt Services

Persistenz wird über **Stores** angebunden, nicht über Services.

```java
public class UserStore {

    private final JpaUserRepository repo;
    private final UserMapper mapper;

    public UserStore(JpaUserRepository repo, UserMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public Optional<User> findById(UserId id) {
        return repo.findById(map(id))
                   .map(mapper::toDomain);
    }
}
```

**Regeln:**
- Stores kapseln Mapping + Repository
- Keine Business-Logik
- Kein Cross-Aggregate-Zugriff

---

## 7. Spring Wiring

- Keine explizite Konfiguration nötig
- Autodiscovery über Packages

```java
@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = JpaUserRepository.class)
@EntityScan(basePackageClasses = JpaUser.class)
```

---

## 8. Austauschbarkeit

Persistenz ist austauschbar durch **Package-Swap**:

```
persistence/jpa
persistence/mongo
persistence/file
```

Die **Domain bleibt unverändert**.

---

## 9. Anti-Patterns (explizit verboten)

❌ `PersistenceService`  
❌ `GenericRepository<T>`  
❌ `BaseEntity`  
❌ `AbstractPersistenceModule`  
❌ `@MappedSuperclass` für Domain  
❌ JPA-Annotationen im Domain-Modell  

Wenn du das brauchst, hast du das Problem falsch geschnitten.

---

## 10. Entscheidungsregel

> Wenn Persistenz ohne Nachdenken „einfach funktioniert“,
> dann ist sie richtig gebaut.

Wenn du darüber diskutieren musst, **ist sie zu abstrakt**.

---

## 11. TL;DR

- Persistenz ist **Konvention**, kein Modul
- Struktur schlägt Abstraktion
- Lesbarkeit schlägt Wiederverwendung
- Austauschbarkeit entsteht durch Disziplin, nicht durch Frameworks
