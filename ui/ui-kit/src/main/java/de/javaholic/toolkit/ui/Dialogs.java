package de.javaholic.toolkit.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.javaholic.toolkit.i18n.I18n;
import de.javaholic.toolkit.i18n.Text;
import de.javaholic.toolkit.i18n.TextRole;
import de.javaholic.toolkit.i18n.Texts;
import de.javaholic.toolkit.ui.form.Forms;
import de.javaholic.toolkit.ui.layout.Layouts;

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
     *     .textConfirm(Texts.label("ok"))
     *     .textCancel()
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
        private Text titleText;
        private Text descriptionText;
        private Text confirmLabelText;
        private Text confirmTooltipText;
        private boolean confirmEnabled;
        private Text cancelLabelText;
        private Text cancelTooltipText;
        private boolean cancelEnabled;
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
         * Sets dialog texts by role (HEADER, DESCRIPTION).
         *
         * <p>Other roles are ignored.</p>
         */
        public GridSelectionDialogBuilder<T> text(Text... texts) {
            HeaderDescriptionSlots slots = headerDescriptionSlots(texts);
            if (slots.header != null) {
                this.titleText = slots.header;
            }
            if (slots.description != null) {
                this.descriptionText = slots.description;
            }
            return this;
        }

        /**
         * Sets confirm texts by role (LABEL, TOOLTIP).
         *
         * <p>Other roles are ignored.</p>
         */
        public GridSelectionDialogBuilder<T> textConfirm(Text... texts) {
            if (texts == null) {
                this.confirmEnabled = true;
                return this;
            }
            this.confirmEnabled = true;
            ButtonTextSlots slots = buttonTextSlots(texts);
            if (slots.label != null) {
                this.confirmLabelText = slots.label;
            }
            if (slots.tooltip != null) {
                this.confirmTooltipText = slots.tooltip;
            }
            return this;
        }

        /**
         * Sets cancel texts by role (LABEL, TOOLTIP).
         *
         * <p>Other roles are ignored.</p>
         */
        public GridSelectionDialogBuilder<T> textCancel(Text... texts) {
            if (texts == null) {
                this.cancelEnabled = true;
                return this;
            }
            this.cancelEnabled = true;
            ButtonTextSlots slots = buttonTextSlots(texts);
            if (slots.label != null) {
                this.cancelLabelText = slots.label;
            }
            if (slots.tooltip != null) {
                this.cancelTooltipText = slots.tooltip;
            }
            return this;
        }

        /**
         * Enables a cancel action with default label.
         */
        public GridSelectionDialogBuilder<T> textCancel() {
            return textCancel((Text[]) null);
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
            if (!confirmEnabled) {
                throw new IllegalStateException("Confirm text not set; call textConfirm(...)");
            }
            Dialog dialog = new Dialog();
            dialog.setModal(true);
            dialog.setCloseOnEsc(false);
            dialog.setCloseOnOutsideClick(false);

            if (titleText != null) {
                dialog.setHeaderTitle(Texts.resolve(i18n, titleText));
            }

            var content = Layouts.vbox();
            if (descriptionText != null) {
                content.add(new Span(Texts.resolve(i18n, descriptionText)));
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

            Text okText = defaultIfNull(confirmLabelText, Texts.label("ok"));
            Text okTooltip = confirmTooltipText;

            Button ok = Buttons.create()
                    .withI18n(i18n)
                    .text(okText, okTooltip)
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
                Text cancelText = defaultIfNull(cancelLabelText, Texts.label("cancel"));
                Button cancel = Buttons.create()
                        .withI18n(i18n)
                        .text(cancelText, cancelTooltipText)
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
     *     .withI18n(i18n)
     *     .text(Texts.header("config.delete.title"))
     *     .text(Texts.description("config.delete.confirmation"))
     *     .textConfirm(Texts.label("delete"))
     *     .textCancel()
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
     *     .textConfirm(Texts.label("save"))
     *     .textCancel()
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
        private final VerticalLayout content;
        private final HorizontalLayout buttons;
        private Span description;

        private BiConsumer<Forms.Form<T>, Dialog> onOk;
        private Runnable onCancel;
        private I18n i18n;
        private Text titleText;
        private Text descriptionText;
        private Text okLabelText = Texts.label("ok");
        private Text okTooltipText;
        private boolean confirmEnabled;
        private Text cancelLabelText = Texts.label("cancel");
        private Text cancelTooltipText;
        private boolean cancelEnabled;

        private FormDialog(Forms.Form<T> form) {
            this.form = form;

            content = Layouts.vbox();
            content.add(form.layout());
            dialog.add(content);

            ok = Buttons.create()
                    .text(this.okLabelText, this.okTooltipText)
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
                    .text(this.cancelLabelText, this.cancelTooltipText)
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
         * Sets i18n used to resolve Text keys.
         */
        public FormDialog<T> withI18n(I18n i18n) {
            this.i18n = i18n;
            applyTexts();
            return this;
        }

        /**
         * Sets dialog texts by role (HEADER, DESCRIPTION).
         *
         * <p>Other roles are ignored.</p>
         */
        public FormDialog<T> text(Text... texts) {
            HeaderDescriptionSlots slots = headerDescriptionSlots(texts);
            if (slots.header != null) {
                this.titleText = slots.header;
            }
            if (slots.description != null) {
                this.descriptionText = slots.description;
            }
            applyTexts();
            return this;
        }

        /**
         * Sets confirm texts by role (LABEL, TOOLTIP).
         *
         * <p>Other roles are ignored.</p>
         */
        public FormDialog<T> textConfirm(Text... texts) {
            if (texts == null) {
                this.confirmEnabled = true;
                applyTexts();
                return this;
            }
            this.confirmEnabled = true;
            ButtonTextSlots slots = buttonTextSlots(texts);
            if (slots.label != null) {
                this.okLabelText = slots.label;
            }
            if (slots.tooltip != null) {
                this.okTooltipText = slots.tooltip;
            }
            applyTexts();
            return this;
        }

        /**
         * Sets cancel texts by role (LABEL, TOOLTIP).
         *
         * <p>Other roles are ignored.</p>
         */
        public FormDialog<T> textCancel(Text... texts) {
            if (texts == null) {
                this.cancelEnabled = true;
                applyTexts();
                return this;
            }
            this.cancelEnabled = true;
            ButtonTextSlots slots = buttonTextSlots(texts);
            if (slots.label != null) {
                this.cancelLabelText = slots.label;
            }
            if (slots.tooltip != null) {
                this.cancelTooltipText = slots.tooltip;
            }
            applyTexts();
            return this;
        }

        /**
         * Enables a cancel action with default label.
         */
        public FormDialog<T> textCancel() {
            return textCancel((Text[]) null);
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
                throw new IllegalStateException("Confirm text not set; call textConfirm(...)");
            }
            dialog.open();
        }

        private void applyTexts() {
            if (titleText != null) {
                dialog.setHeaderTitle(Texts.resolve(i18n, titleText));
            }
            if (descriptionText != null) {
                if (description == null) {
                    description = new Span();
                    content.addComponentAsFirst(description);
                }
                description.setText(Texts.resolve(i18n, descriptionText));
            } else if (description != null) {
                content.remove(description);
                description = null;
            }
            ok.setText(Texts.resolve(i18n, defaultIfNull(okLabelText, Texts.label("ok"))));
            if (okTooltipText != null) {
                ok.setTooltipText(Texts.resolve(i18n, okTooltipText));
            }
            cancel.setText(Texts.resolve(i18n, defaultIfNull(cancelLabelText, Texts.label("cancel"))));
            if (cancelTooltipText != null) {
                cancel.setTooltipText(Texts.resolve(i18n, cancelTooltipText));
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
        private I18n i18n;
        private Text titleText;
        private Text descriptionText;
        private Text confirmLabelText = Texts.label("ok");
        private Text confirmTooltipText;
        private boolean confirmEnabled;
        private Text cancelLabelText = Texts.label("cancel");
        private Text cancelTooltipText;
        private boolean cancelEnabled;

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
         * Sets dialog texts by role (HEADER, DESCRIPTION).
         *
         * <p>Other roles are ignored.</p>
         */
        public ConfirmDialogBuilder text(Text... texts) {
            HeaderDescriptionSlots slots = headerDescriptionSlots(texts);
            if (slots.header != null) {
                this.titleText = slots.header;
            }
            if (slots.description != null) {
                this.descriptionText = slots.description;
            }
            return this;
        }

        /**
         * Sets confirm texts by role (LABEL, TOOLTIP).
         *
         * <p>Other roles are ignored.</p>
         */
        public ConfirmDialogBuilder textConfirm(Text... texts) {
            if (texts == null) {
                this.confirmEnabled = true;
                return this;
            }
            this.confirmEnabled = true;
            ButtonTextSlots slots = buttonTextSlots(texts);
            if (slots.label != null) {
                this.confirmLabelText = slots.label;
            }
            if (slots.tooltip != null) {
                this.confirmTooltipText = slots.tooltip;
            }
            return this;
        }

        /**
         * Sets cancel texts by role (LABEL, TOOLTIP).
         *
         * <p>Other roles are ignored.</p>
         */
        public ConfirmDialogBuilder textCancel(Text... texts) {
            if (texts == null) {
                this.cancelEnabled = true;
                return this;
            }
            this.cancelEnabled = true;
            ButtonTextSlots slots = buttonTextSlots(texts);
            if (slots.label != null) {
                this.cancelLabelText = slots.label;
            }
            if (slots.tooltip != null) {
                this.cancelTooltipText = slots.tooltip;
            }
            return this;
        }

        /**
         * Enables a cancel action with default label.
         */
        public ConfirmDialogBuilder textCancel() {
            return textCancel((Text[]) null);
        }

        /**
         * Opens the dialog.
         */
        public void open(Consumer<Boolean> completion) {
            if (!confirmEnabled) {
                throw new IllegalStateException("Confirm text not set; call textConfirm(...)");
            }
            if (titleText != null) {
                dialog.setHeaderTitle(Texts.resolve(i18n, titleText));
            }

            var content = Layouts.vbox();
            if (descriptionText != null) {
                content.add(new Span(Texts.resolve(i18n, descriptionText)));
            }

            if (content.getComponentCount() > 0) {
                dialog.add(content);
            }

            Button ok = Buttons.create()
                    .withI18n(i18n)
                    .text(defaultIfNull(confirmLabelText, Texts.label("ok")), confirmTooltipText)
                    .action(() -> {
                        dialog.close();
                        completion.accept(true);
                    })
                    .build();

            dialog.getFooter().removeAll();
            if (cancelEnabled) {
                Button cancel = Buttons.create()
                        .withI18n(i18n)
                        .text(defaultIfNull(cancelLabelText, Texts.label("cancel")), cancelTooltipText)
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

    private static Text defaultIfNull(Text value, Text fallback) {
        return value != null ? value : fallback;
    }

    private static ButtonTextSlots buttonTextSlots(Text... texts) {
        if (texts == null) {
            return ButtonTextSlots.empty();
        }
        Text label = null;
        Text tooltip = null;
        for (Text text : texts) {
            if (text == null) {
                continue;
            }
            if (text.role() == TextRole.LABEL) {
                label = text;
            } else if (text.role() == TextRole.TOOLTIP) {
                tooltip = text;
            }
        }
        return new ButtonTextSlots(label, tooltip);
    }

    private static HeaderDescriptionSlots headerDescriptionSlots(Text... texts) {
        if (texts == null) {
            return HeaderDescriptionSlots.empty();
        }
        Text header = null;
        Text description = null;
        for (Text text : texts) {
            if (text == null) {
                continue;
            }
            if (text.role() == TextRole.HEADER) {
                header = text;
            } else if (text.role() == TextRole.DESCRIPTION) {
                description = text;
            }
        }
        return new HeaderDescriptionSlots(header, description);
    }

    private record ButtonTextSlots(Text label, Text tooltip) {
        private static ButtonTextSlots empty() {
            return new ButtonTextSlots(null, null);
        }
    }

    private record HeaderDescriptionSlots(Text header, Text description) {
        private static HeaderDescriptionSlots empty() {
            return new HeaderDescriptionSlots(null, null);
        }
    }
}
