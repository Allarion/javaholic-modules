# Fluent UI – Roadmap zum produktiven Unterbau

## Ziel
Ein konventionsbasierter UI- und Persistence-Unterbau für Vaadin-Anwendungen,
der Rapid Development ermöglicht, ohne ein eigenes Framework zu bauen.

---

## Architekturüberblick

Vaadin Core (Grid, Binder, Components)
↓
Fluent UI Layer (Grids.of, Layouts.box, Inputs.*)
↓
Convention Layer (neu)
  - FieldRegistry
  - Forms.of(Class<T>)
  - Validation Bridge
  - Persistence by Convention
  - MiniCrudView
↓
Anwendung (IAM, AdminWorkbench, EnvCtrl UI, etc.)

---

## Phase 0 – Leitplanken
- Kein eigener Lifecycle
- Kein eigener State
- Alles optional & überschreibbar

---

## Phase 1 – FieldRegistry (Fundament)

Ziel:
- Zentrale Zuordnung Datentyp → UI Component
- Nutzung von Reflection (kein Getter/Setter nötig)
- Konfigurierbar + overridable

Features:
- Default-Mappings (Code)
- Config Overrides (application.yaml)
- Runtime Overrides (API)

Ergebnis:
- Jedes DTO kann automatisch UI-Felder erzeugen

---

## Phase 2 – Forms.of(Class<T>)

Ziel:
- Automatische Formularerzeugung aus DTOs

Features:
- Reflection über Properties
- FieldRegistry-Integration
- Binder-Anbindung
- Minimal-Layout

Ergebnis:
- DTO → Editierbares Formular ohne Boilerplate

---

## Phase 3 – Validation Bridge

Ziel:
- Bean Validation direkt im UI sichtbar

Features:
- ValidationStatusHandler
- i18n-Key-Mapping
- Feldmarkierung

Ergebnis:
- Professionelles, konsistentes Formularverhalten

---

## Phase 4 – Persistence by Convention

Ziel:
- Speicherung ohne explizite Infrastrukturverkabelung

Konvention:
- DTO + Entity + Repository + Mapper vorhanden
→ CrudStore<T> automatisch verfügbar

Ergebnis:
- UI speichert Daten ohne Wissen über JPA

---

## Phase 5 – MiniCrudView

Ziel:
- Wiederverwendbare CRUD-Ansicht

Features:
- Grid
- Dialog(Form)
- Standard-Actions (New/Edit/Delete)

Ergebnis:
- 80–90 % von Vaadin Pro, aber offen & kontrollierbar

---

## Status
Nach Phase 5:
→ Produktiver Unterbau abgeschlossen
→ Alles Weitere ist Nice-to-have
