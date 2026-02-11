package de.javaholic.toolkit.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.javaholic.toolkit.i18n.I18n;
import de.javaholic.toolkit.i18n.Text;
import de.javaholic.toolkit.i18n.TextRole;
import de.javaholic.toolkit.i18n.Texts;
import de.javaholic.toolkit.ui.form.Forms;

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
     *     .withI18n(i18n)
     *     .text(Texts.header("user.select.title"))
     *     .confirm(Texts.label("ok"))
     *     .cancel(Texts.label("cancel"))
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
        private I18n i18n;
        private Text title;
        private Text confirmText;
        private Text cancelText;
        private Component[] extraContent;

        private GridSelectionDialogBuilder(Grid<T> grid) {
            this.grid = grid;
        }

        /**
         * Sets i18n used to resolve Text keys.
         */
        public GridSelectionDialogBuilder<T> withI18n(I18n i18n) {
            this.i18n = i18n;
            return this;
        }

        /**
         * Sets the dialog text by role (HEADER only).
         */
        public GridSelectionDialogBuilder<T> text(Text text) {
            if (text.role() != TextRole.HEADER) {
                throw new IllegalArgumentException("Grid selection dialog supports HEADER only");
            }
            this.title = text;
            return this;
        }

        /**
         * Sets the confirm button label.
         */
        public GridSelectionDialogBuilder<T> confirm(Text text) {
            this.confirmText = text;
            return this;
        }

        /**
         * Sets the cancel button label.
         */
        public GridSelectionDialogBuilder<T> cancel(Text text) {
            this.cancelText = text;
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
         *   <li>{@link Optional#empty()} if cancelled</li>
         *   <li>{@link Optional#of(Object)} if confirmed with a selection</li>
         * </ul>
         *
         * @param completion selection result consumer
         */
        public void open(Consumer<Optional<T>> completion) {
            Dialog dialog = new Dialog();
            dialog.setModal(true);
            dialog.setCloseOnEsc(false);
            dialog.setCloseOnOutsideClick(false);

            if (title != null) {
                dialog.setHeaderTitle(Texts.resolve(i18n, title));
            }

            VerticalLayout content = new VerticalLayout();
            content.setPadding(false);
            content.setSpacing(true);

            if (extraContent != null) {
                content.add(extraContent);
            }

            content.add(grid);
            dialog.add(content);

            final Selection<T> selection = new Selection<>();

            grid.addSelectionListener(e ->
                    selection.value = e.getFirstSelectedItem().orElse(null)
            );

            Text cancelText = this.cancelText != null ? this.cancelText : Texts.label("Cancel");
            Text okText = this.confirmText != null ? this.confirmText : Texts.label("OK");

            Button cancel = Buttons.create()
                    .withI18n(i18n)
                    .text(cancelText)
                    .action(() -> {
                        dialog.close();
                        completion.accept(Optional.empty());
                    })
                    .build();

            Button ok = Buttons.create()
                    .withI18n(i18n)
                    .text(okText)
                    .action(() -> {
                        dialog.close();
                        completion.accept(Optional.ofNullable(selection.value));
                    })
                    .build();
            ok.setEnabled(false);

            grid.addSelectionListener(e ->
                    ok.setEnabled(e.getFirstSelectedItem().isPresent())
            );

            dialog.getFooter().add(cancel, ok);
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
     *     .withI18n(i18n)
     *     .text(Texts.header("config.delete.title"))
     *     .text(Texts.description("config.delete.confirmation"))
     *     .confirm(Texts.label("delete"))
     *     .cancel(Texts.label("cancel"))
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
     *              f.component(Inputs.text().widthFull().build());
     *              f.label(Texts.label("user.name"));
     *              f.validate(b -> b.asRequired(Texts.resolve(i18n, Texts.error("user.name.required"))));
     *          })
     *          .build();
     *
     * Dialogs.form(form)
     *     .withI18n(i18n)
     *     .text(Texts.header("user.edit.title"))
     *     .confirm(Texts.label("save"))
     *     .cancel(Texts.label("cancel"))
     *     .onOk(f -> save(f.binder().getBean()))
     *     .open();
     * }</pre>
     */
    public static <T> FormDialog<T> form(Forms.Form<T> form) {
        return new FormDialog<>(form);
    }

    /**
     * Same as {@link #form(Forms.Form)} but with i18n.
     */
    public static <T> FormDialog<T> form(Forms.Form<T> form, I18n i18n) {
        return new FormDialog<>(form).withI18n(i18n);
    }

    public static final class FormDialog<T> {

        private final Dialog dialog = new Dialog();
        private final Forms.Form<T> form;
        private final Button ok;
        private final Button cancel;

        private BiConsumer<Forms.Form<T>, Dialog> onOk;
        private Runnable onCancel;
        private I18n i18n;
        private Text title;
        private Text okText = Texts.label("OK");
        private Text cancelText = Texts.label("Cancel");

        private FormDialog(Forms.Form<T> form) {
            this.form = form;

            dialog.add(form.layout());

            ok = Buttons.create()
                    .text(this.okText)
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
                    .text(this.cancelText)
                    .build();

            cancel.addClickListener(e -> {
                if (onCancel != null) {
                    onCancel.run();
                }
                dialog.close();
            });

            HorizontalLayout buttons = new HorizontalLayout(cancel, ok);
            dialog.add(buttons);
        }

        /**
         * Sets i18n used to resolve Text keys.
         */
        public FormDialog<T> withI18n(I18n i18n) {
            this.i18n = i18n;
            applyTexts();
            return this;
        }

        /**
         * Sets the dialog text by role (HEADER only).
         */
        public FormDialog<T> text(Text text) {
            if (text.role() != TextRole.HEADER) {
                throw new IllegalArgumentException("Form dialog supports HEADER only");
            }
            this.title = text;
            applyTexts();
            return this;
        }

        /**
         * Sets the confirm button label.
         */
        public FormDialog<T> confirm(Text text) {
            this.okText = text;
            applyTexts();
            return this;
        }

        /**
         * Sets the cancel button label.
         */
        public FormDialog<T> cancel(Text text) {
            this.cancelText = text;
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
            dialog.open();
        }

        private void applyTexts() {
            if (title != null) {
                dialog.setHeaderTitle(Texts.resolve(i18n, title));
            }
            ok.setText(Texts.resolve(i18n, okText));
            cancel.setText(Texts.resolve(i18n, cancelText));
        }
    }

    public static final class ConfirmDialogBuilder {

        private final Dialog dialog = new Dialog();
        private I18n i18n;
        private Text title;
        private Text message;
        private Text okText = Texts.label("OK");
        private Text cancelText = Texts.label("Cancel");

        private ConfirmDialogBuilder() {
            dialog.setModal(true);
            dialog.setCloseOnEsc(false);
            dialog.setCloseOnOutsideClick(false);
        }

        /**
         * Sets i18n used to resolve Text keys.
         */
        public ConfirmDialogBuilder withI18n(I18n i18n) {
            this.i18n = i18n;
            return this;
        }

        /**
         * Sets dialog text by role (HEADER and DESCRIPTION).
         */
        public ConfirmDialogBuilder text(Text text) {
            if (text.role() == TextRole.HEADER) {
                this.title = text;
                return this;
            }
            if (text.role() == TextRole.DESCRIPTION) {
                this.message = text;
                return this;
            }
            throw new IllegalArgumentException("Confirm dialog supports HEADER and DESCRIPTION only");
        }

        /**
         * Sets the confirm button label.
         */
        public ConfirmDialogBuilder confirm(Text text) {
            this.okText = text;
            return this;
        }

        /**
         * Sets the cancel button label.
         */
        public ConfirmDialogBuilder cancel(Text text) {
            this.cancelText = text;
            return this;
        }

        /**
         * Opens the dialog.
         */
        public void open(Consumer<Boolean> completion) {
            if (title != null) {
                dialog.setHeaderTitle(Texts.resolve(i18n, title));
            }
            if (message != null) {
                dialog.add(Texts.resolve(i18n, message));
            }

            Button cancel = Buttons.create()
                    .withI18n(i18n)
                    .text(cancelText)
                    .action(() -> {
                        dialog.close();
                        completion.accept(false);
                    })
                    .build();

            Button ok = Buttons.create()
                    .withI18n(i18n)
                    .text(okText)
                    .action(() -> {
                        dialog.close();
                        completion.accept(true);
                    })
                    .build();

            dialog.getFooter().removeAll();
            dialog.getFooter().add(cancel, ok);
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
}
