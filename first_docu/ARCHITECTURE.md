# UI Toolkit Architecture (Grids / Forms / CRUD)

## Ziel
Boilerplate-Reduktion für Vaadin UI durch konsistente, wiederverwendbare Fluent APIs.
Das Toolkit soll:
- Rapid-Prototyping ermöglichen (auch direkt auf Domain/JPA/DTO nutzbar),
- aber gleichzeitig saubere Architektur (DDD/Hex/DTO-first) nicht verhindern,
- und vor allem: gleiche Probleme (hidden fields, defaults, ordering, labels) zentral lösen.

## Kernprinzip
**UI-Komponenten (Grids, Forms, CrudPanel) dürfen keine technischen Schichten erraten.**
Sie arbeiten ausschließlich mit einem **UI-semantischen Metamodell**: `UiMeta<T>`.

Das reduziert:
- verteilte Sonderlogik (z.B. hidden id/version in CrudPanel),
- inkonsistentes Verhalten zwischen Grid/Form,
- „warum zeigt er mir UUID?“-Debatten.

## Schichten

### 1) Technical Introspection Layer (rein technisch)
**Pakete/Typen:**
- `BeanMeta<T>`
- `BeanProperty<T, V>`
- `BeanIntrospector`

**Zuständigkeit:**
- Reflection/Introspection: Properties, Getter/Setter, Typen
- Optional: technische Erkennung von `idProperty()` / `versionProperty()` (wenn möglich)

**Nicht erlaubt (No-Go):**
- UI-Semantik: hidden/label/order/readOnly
- Vaadin-spezifische Entscheidungen

> Motto: BeanMeta ist ein „AST“ für Beans – nicht mehr.

### 2) UI Semantic Layer (zentrale UI-Regeln)
**Pakete/Typen:**
- `UiMeta<T>`
- `UiProperty<T>`
- `UiInspector`

**Zuständigkeit:**
- Einheitliche UI-Regeln für Sichtbarkeit & Darstellung:
    - visible/hidden defaults
    - Reihenfolge (order)
    - Label (später i18n-fähig)
    - readOnly (später)

**Phase 1 (Defaults-only):**
- `idProperty()` ist hidden (falls vorhanden)
- `versionProperty()` ist hidden (falls vorhanden)
- alle anderen Properties sichtbar

**TODO (Phase 2 / B):**
- UI-Annotationen wie `@UiHidden`, `@UiLabel`, `@UiOrder`, `@UiReadOnly`
- optional: CrudMode-spezifische Defaults (RAPID vs CLEAN)

**Wichtig:**
- Grid/Form/CrudPanel dürfen keine eigene Hidden-Logik haben.
- Jede Entscheidung „soll UI das sehen?“ lebt in `UiMeta`.

### 3) Fluent API Layer (benutzerfreundlich)
**Entry Points:**
- `Grids.of(type)` → manuell
- `Grids.auto(type)` → auto builder über `UiMeta`
- `Forms.of(type)` → manuell
- `Forms.auto(type)` → auto builder über `UiMeta`
- `CrudPanels.of(type)...build()` → manueller CRUD builder
- `CrudPanels.auto(type)...build()` → UiMeta-basierter Auto-CRUD builder

**Builder müssen Overrides unterstützen (ab Phase 1):**
- `.exclude("password")`
- `.include("...")` (optional)
- `.override("field", ...)`
- `.order(...)` (optional)
- `.label(...)` (optional)

Overrides sind UI-semantisch und gehören daher in Builder/UiMeta-Config, nicht in CrudPanel.

### 4) Usecase Layer (CRUD Orchestration)
**Typ:**
- `CrudPanel<T>`

**Zuständigkeit:**
- Orchestriert UI: Grid + Form + Actions (Edit/Delete/etc)
- Delegiert Rendering/Meta komplett an Grids/Forms (auto oder manual)

**No-Go:**
- Keine BeanMeta/BeanIntrospector-Nutzung mehr
- Keine hidden/id/version Filter hier
- Keine Auto-Column-Logik hier

> CrudPanel ist „Controller“, nicht „Renderer“.

## Konsistenzregeln
1. **Single source of truth:** UI-Sichtbarkeit/Defaults ausschließlich via UiMeta.
2. **No duplication:** Hidden/Order/Label-Logik nie in mehreren Komponenten.
3. **Builder overrides first-class:** Overrides gehören in Builder, nicht in Panels.
4. **Keep Fluent API simple:** `.of()` bleibt manuell, `.auto()` bleibt bequem.

## Langfristiges Ziel (Roadmap grob)
- Phase 1: UiMeta Defaults-only + auto builders + CrudPanel entkoppeln
- Phase 2: UI-Annotationen + (optional) CrudMode RAPID/CLEAN
- Phase 3: i18n Integration (Texts/Keys) ohne API-Wildwuchs
- Phase 4: Optional: DTO-first scaffolding / Mapping helpers (außerhalb UI)

## Package Boundaries

- introspection → rein technisch
- ui.meta → UI-Semantik
- ui.grid/ui.form → Rendering
- ui.crud → Orchestrierung

Cross-imports, die diese Trennung verletzen, sind Architekturf ehler.

## Override Strategy

Auto-Builders unterstützen:
- exclude(String...)
- override(String, Consumer<...>)

Overrides werden nach Default-Erstellung angewendet.
UiMeta bleibt die zentrale Entscheidungsinstanz für Sichtbarkeit.
