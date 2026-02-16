package de.javaholic.toolkit.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.javaholic.toolkit.i18n.TextResolver;
import de.javaholic.toolkit.ui.form.Forms;
import de.javaholic.toolkit.ui.layout.Layouts;
import de.javaholic.toolkit.i18n.DefaultTextResolver;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Fluent factory for common dialog patterns based on Vaadin {@link Dialog}.
 * <p>
 * This utility intentionally relies only on Vaadin standard dialog features
 * (header, footer, modal handling) and avoids any custom dialog framework.
 *
 */
public final class Dialogs {

    private Dialogs() {
        // utility class
    }

    // =====================================================================
    // SELECT DIALOG
    // =====================================================================

    /**
     * Creates a dialog for selecting a single item from a {@link Grid}.
     *
     * <h3>How to use</h3>
     * <pre>{@code
     * Dialogs.select(userGrid)
     *     .withTextResolver(key -> messages.getOrDefault(key, key))
     *     .header("user.select.title")
     *     .confirmLabel("ok")
     *     .cancelLabel("cancel")
     *     .open(result -> result.ifPresent(this::handleUser));
     * }</pre>
     *
     * @param grid the grid used for selection
     * @param <T>  grid item type
     * @return a fluent dialog builder
     */
    public static <T> GridSelectionDialogBuilder<T> select(Grid<T> grid) {
        return new GridSelectionDialogBuilder<>(grid);
    }

    /**
     * Builder for a grid-based selection dialog.
     *
     * @param <T> item type
     */
    public static final class GridSelectionDialogBuilder<T> {

        private final Grid<T> grid;
        // Dialogs are containers: they own one resolver and pass it to nested UI builders.
        private TextResolver textResolver = new DefaultTextResolver();
        private String titleKey;
        private String descriptionKey;
        private String confirmLabelKey;
        private String confirmTooltipKey;
        private boolean confirmEnabled;
        private String cancelLabelKey;
        private String cancelTooltipKey;
        private boolean cancelEnabled;
        private Component[] extraContent;

        private GridSelectionDialogBuilder(Grid<T> grid) {
            this.grid = grid;
        }

        /**
         * Sets the resolver used for label keys.
         */
        public GridSelectionDialogBuilder<T> withTextResolver(TextResolver textResolver) {
            this.textResolver = java.util.Objects.requireNonNull(textResolver, "textResolver");
            return this;
        }

        public GridSelectionDialogBuilder<T> header(String key) {
            this.titleKey = key;
            return this;
        }

        public GridSelectionDialogBuilder<T> description(String key) {
            this.descriptionKey = key;
            return this;
        }

        public GridSelectionDialogBuilder<T> confirmLabel(String key) {
            this.confirmEnabled = true;
            this.confirmLabelKey = key;
            return this;
        }

        public GridSelectionDialogBuilder<T> confirmTooltip(String key) {
            this.confirmEnabled = true;
            this.confirmTooltipKey = key;
            return this;
        }

        public GridSelectionDialogBuilder<T> cancelLabel(String key) {
            this.cancelEnabled = true;
            this.cancelLabelKey = key;
            return this;
        }

        public GridSelectionDialogBuilder<T> cancelTooltip(String key) {
            this.cancelEnabled = true;
            this.cancelTooltipKey = key;
            return this;
        }

        /**
         * Adds additional components above the grid.
         * <p>
         * Intended for explanatory text, filters or future {@code Inputs}.
         *
         * @param components additional content components
         * @return this builder
         */
        public GridSelectionDialogBuilder<T> withContent(Component... components) {
            this.extraContent = components;
            return this;
        }

        /**
         * Opens the dialog.
         * <p>
         * The completion callback is invoked exactly once:
         * <ul>
         *   <li>{@link Optional#empty()} if canceled</li>
         *   <li>{@link Optional#of(Object)} if confirmed with a selection</li>
         * </ul>
         *
         * @param completion selection result consumer
         */
        public void open(Consumer<Optional<T>> completion) {
            if (!confirmEnabled) {
                throw new IllegalStateException("Confirm label not set; call confirmLabel(...)");
            }
            Dialog dialog = new Dialog();
            dialog.setModal(true);
            dialog.setCloseOnEsc(false);
            dialog.setCloseOnOutsideClick(false);

            if (titleKey != null) {
                dialog.setHeaderTitle(resolve(textResolver, titleKey));
            }

            var content = Layouts.vbox();
            if (descriptionKey != null) {
                content.add(new Span(resolve(textResolver, descriptionKey)));
            }
            if (extraContent != null) {
                content.add(extraContent);
            }
            content.add(grid);
            dialog.add(content);

            final Selection<T> selection = new Selection<>();

            grid.addSelectionListener(e ->
                    selection.value = e.getFirstSelectedItem().orElse(null)
            );

            Button ok = Buttons.create()
                    .withTextResolver(textResolver)
                    .label(defaultIfNull(confirmLabelKey, "ok"))
                    .tooltip(confirmTooltipKey)
                    .action(() -> {
                        dialog.close();
                        completion.accept(Optional.ofNullable(selection.value));
                    })
                    .build();
            ok.setEnabled(false);

            grid.addSelectionListener(e ->
                    ok.setEnabled(e.getFirstSelectedItem().isPresent())
            );

            if (cancelEnabled) {
                Button cancel = Buttons.create()
                        .withTextResolver(textResolver)
                        .label(defaultIfNull(cancelLabelKey, "cancel"))
                        .tooltip(cancelTooltipKey)
                        .action(() -> {
                            dialog.close();
                            completion.accept(Optional.empty());
                        })
                        .build();
                dialog.getFooter().add(Layouts.hbox(cancel, ok));
            } else {
                dialog.getFooter().add(Layouts.hbox(ok));
            }
            dialog.open();
        }
    }

    // =====================================================================
    // CONFIRM DIALOG
    // =====================================================================

    /**
     * Opens a simple confirmation dialog.
     *
     * <h3>How to use</h3>
     * <pre>{@code
     * Dialogs.confirm()
     *     .withTextResolver(key -> messages.getOrDefault(key, key))
     *     .header("config.delete.title")
     *     .description("config.delete.confirmation")
     *     .confirmLabel("delete")
     *     .cancelLabel("cancel")
     *     .open(confirmed -> {
     *         if (confirmed) {
     *             deleteConfig();
     *         }
     *     });
     * }</pre>
     *
     * @return a fluent dialog builder
     */
    public static ConfirmDialogBuilder confirm() {
        return new ConfirmDialogBuilder();
    }

    // =====================================================================
    // FORM DIALOG
    // =====================================================================

    /**
     * Opens a dialog containing a {@link Forms.Form}.
     * <p>
     * The OK button only closes the dialog if the binder validates successfully.
     * Validation uses Bean Validation annotations on the
     * bound bean (e.g. {@code @NotNull}, {@code @Min}, {@code @Max}).
     *
     * <pre>{@code
     * Form<UserConfig> form =
     *     Forms.of(UserConfig.class)
     *          .field("name", f -> {
     *              f.component(Inputs.textField().widthFull().build());
     *              f.label("user.name");
     *              f.validate(b -> b.asRequired("user.name.required"));
     *          })
     *          .build();
     *
     * Dialogs.form(form)
     *     .withTextResolver(key -> messages.getOrDefault(key, key))
     *     .header("user.edit.title")
     *     .confirmLabel("save")
     *     .cancelLabel("cancel")
     *     .onOk(f -> save(f.binder().getBean()))
     *     .open();
     * }</pre>
     */
    public static <T> FormDialog<T> form(Forms.Form<T> form) {
        return new FormDialog<>(form);
    }

    public static final class FormDialog<T> {

        private final Dialog dialog = new Dialog();
        private final Forms.Form<T> form;
        private final Button ok;
        private final Button cancel;
        private final VerticalLayout content;
        private final HorizontalLayout buttons;
        private Span description;

        private BiConsumer<Forms.Form<T>, Dialog> onOk;
        private Runnable onCancel;
        private TextResolver textResolver = new DefaultTextResolver();
        private String titleKey;
        private String descriptionKey;
        private String okLabelKey = "ok";
        private String okTooltipKey;
        private boolean confirmEnabled;
        private String cancelLabelKey = "cancel";
        private String cancelTooltipKey;
        private boolean cancelEnabled;

        private FormDialog(Forms.Form<T> form) {
            this.form = form;

            content = Layouts.vbox();
            content.add(form.layout());
            dialog.add(content);

            ok = Buttons.create()
                    .label(this.okLabelKey)
                    .tooltip(this.okTooltipKey)
                    .enabledWhen(() -> form.binder().validate().isOk())
                        .revalidateOn(r -> form.binder().addStatusChangeListener(e -> r.run()))
                        .done()
                    .action(() -> {
                        if (!form.binder().validate().isOk()) {
                            return;
                        }
                        if (onOk != null) {
                            onOk.accept(form, dialog);
                        } else {
                            dialog.close();
                        }
                    })
                    .build();

            cancel = Buttons.create()
                    .label(this.cancelLabelKey)
                    .tooltip(this.cancelTooltipKey)
                    .build();

            cancel.addClickListener(e -> {
                if (onCancel != null) {
                    onCancel.run();
                }
                dialog.close();
            });

            buttons = Layouts.hbox(ok);
            dialog.getFooter().add(buttons);
        }

        /**
         * Sets the resolver used for label keys.
         */
        public FormDialog<T> withTextResolver(TextResolver textResolver) {
            this.textResolver = java.util.Objects.requireNonNull(textResolver, "textResolver");
            applyTexts();
            return this;
        }

        public FormDialog<T> header(String key) {
            this.titleKey = key;
            applyTexts();
            return this;
        }

        public FormDialog<T> description(String key) {
            this.descriptionKey = key;
            applyTexts();
            return this;
        }

        public FormDialog<T> confirmLabel(String key) {
            this.confirmEnabled = true;
            this.okLabelKey = key;
            applyTexts();
            return this;
        }

        public FormDialog<T> confirmTooltip(String key) {
            this.confirmEnabled = true;
            this.okTooltipKey = key;
            applyTexts();
            return this;
        }

        public FormDialog<T> cancelLabel(String key) {
            this.cancelEnabled = true;
            this.cancelLabelKey = key;
            applyTexts();
            return this;
        }

        public FormDialog<T> cancelTooltip(String key) {
            this.cancelEnabled = true;
            this.cancelTooltipKey = key;
            applyTexts();
            return this;
        }

        public FormDialog<T> onOk(Consumer<Forms.Form<T>> handler) {
            this.onOk = (f, d) -> {
                handler.accept(f);
                d.close();
            };
            return this;
        }

        public FormDialog<T> onOk(BiConsumer<Forms.Form<T>, Dialog> handler) {
            this.onOk = handler;
            return this;
        }

        public FormDialog<T> onCancel(Runnable handler) {
            this.onCancel = handler;
            return this;
        }

        public Dialog dialog() {
            return dialog;
        }

        public void open() {
            if (!confirmEnabled) {
                throw new IllegalStateException("Confirm label not set; call confirmLabel(...)");
            }
            dialog.open();
        }

        private void applyTexts() {
            if (titleKey != null) {
                dialog.setHeaderTitle(resolve(textResolver, titleKey));
            }
            if (descriptionKey != null) {
                if (description == null) {
                    description = new Span();
                    content.addComponentAsFirst(description);
                }
                description.setText(resolve(textResolver, descriptionKey));
            } else if (description != null) {
                content.remove(description);
                description = null;
            }
            ok.setText(resolve(textResolver, defaultIfNull(okLabelKey, "ok")));
            if (okTooltipKey != null) {
                ok.setTooltipText(resolve(textResolver, okTooltipKey));
            }
            cancel.setText(resolve(textResolver, defaultIfNull(cancelLabelKey, "cancel")));
            if (cancelTooltipKey != null) {
                cancel.setTooltipText(resolve(textResolver, cancelTooltipKey));
            }
            buttons.removeAll();
            if (cancelEnabled) {
                buttons.add(cancel, ok);
            } else {
                buttons.add(ok);
            }
        }
    }

    public static final class ConfirmDialogBuilder {

        private final Dialog dialog = new Dialog();
        private TextResolver textResolver = new DefaultTextResolver();
        private String titleKey;
        private String descriptionKey;
        private String confirmLabelKey = "ok";
        private String confirmTooltipKey;
        private boolean confirmEnabled;
        private String cancelLabelKey = "cancel";
        private String cancelTooltipKey;
        private boolean cancelEnabled;

        private ConfirmDialogBuilder() {
            dialog.setModal(true);
            dialog.setCloseOnEsc(false);
            dialog.setCloseOnOutsideClick(false);
        }

        /**
         * Sets the resolver used for label keys.
         */
        public ConfirmDialogBuilder withTextResolver(TextResolver textResolver) {
            this.textResolver = java.util.Objects.requireNonNull(textResolver, "textResolver");
            return this;
        }

        public ConfirmDialogBuilder header(String key) {
            this.titleKey = key;
            return this;
        }

        public ConfirmDialogBuilder description(String key) {
            this.descriptionKey = key;
            return this;
        }

        public ConfirmDialogBuilder confirmLabel(String key) {
            this.confirmEnabled = true;
            this.confirmLabelKey = key;
            return this;
        }

        public ConfirmDialogBuilder confirmTooltip(String key) {
            this.confirmEnabled = true;
            this.confirmTooltipKey = key;
            return this;
        }

        public ConfirmDialogBuilder cancelLabel(String key) {
            this.cancelEnabled = true;
            this.cancelLabelKey = key;
            return this;
        }

        public ConfirmDialogBuilder cancelTooltip(String key) {
            this.cancelEnabled = true;
            this.cancelTooltipKey = key;
            return this;
        }

        /**
         * Opens the dialog.
         */
        public void open(Consumer<Boolean> completion) {
            if (!confirmEnabled) {
                throw new IllegalStateException("Confirm label not set; call confirmLabel(...)");
            }
            if (titleKey != null) {
                dialog.setHeaderTitle(resolve(textResolver, titleKey));
            }

            var content = Layouts.vbox();
            if (descriptionKey != null) {
                content.add(new Span(resolve(textResolver, descriptionKey)));
            }

            if (content.getComponentCount() > 0) {
                dialog.add(content);
            }

            Button ok = Buttons.create()
                    .withTextResolver(textResolver)
                    .label(defaultIfNull(confirmLabelKey, "ok"))
                    .tooltip(confirmTooltipKey)
                    .action(() -> {
                        dialog.close();
                        completion.accept(true);
                    })
                    .build();

            dialog.getFooter().removeAll();
            if (cancelEnabled) {
                Button cancel = Buttons.create()
                        .withTextResolver(textResolver)
                        .label(defaultIfNull(cancelLabelKey, "cancel"))
                        .tooltip(cancelTooltipKey)
                        .action(() -> {
                            dialog.close();
                            completion.accept(false);
                        })
                        .build();
                dialog.getFooter().add(Layouts.hbox(cancel, ok));
            } else {
                dialog.getFooter().add(Layouts.hbox(ok));
            }
            dialog.open();
        }
    }

    /**
     * Simple mutable reference used for temporary UI state.
     * <p>
     * This intentionally avoids {@link java.util.concurrent.atomic.AtomicReference}
     * and {@link java.lang.ref.WeakReference}, as neither concurrency nor GC-based
     * lifecycle management is desired here.
     */
    private static final class Selection<T> {
        T value;
    }

    private static String defaultIfNull(String value, String fallback) {
        return value != null ? value : fallback;
    }

    private static String resolve(TextResolver resolver, String key) {
        return resolver.resolve(key).orElse(key);
    }

}


