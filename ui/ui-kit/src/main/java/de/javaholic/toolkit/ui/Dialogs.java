package de.javaholic.toolkit.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.javaholic.toolkit.ui.form.Form;

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
     *     .texts(DialogTexts.of("Select user", "OK", "Cancel"))
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
        private DialogTexts texts;
        private Component[] extraContent;

        private GridSelectionDialogBuilder(Grid<T> grid) {
            this.grid = grid;
        }

        /**
         * Sets dialog texts such as title and button labels.
         *
         * @param texts dialog texts
         * @return this builder
         */
        public GridSelectionDialogBuilder<T> texts(DialogTexts texts) {
            this.texts = texts;
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

            if (texts != null && texts.title != null) {
                dialog.setHeaderTitle(texts.title);
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

            Button cancel = new Button(
                    texts != null ? texts.cancelText : "Cancel",
                    e -> {
                        dialog.close();
                        completion.accept(Optional.empty());
                    }
            );

            Button ok = new Button(
                    texts != null ? texts.okText : "OK",
                    e -> {
                        dialog.close();
                        completion.accept(Optional.ofNullable(selection.value));
                    }
            );
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
     * Dialogs.confirm(
     *     DialogTexts.of("Delete configuration", "Delete", "Cancel"),
     *     "Do you really want to delete this configuration?",
     *     confirmed -> {
     *         if (confirmed) {
     *             deleteConfig();
     *         }
     *     }
     * );
     * }</pre>
     *
     * @param texts      dialog texts (title, ok, cancel)
     * @param message    confirmation message
     * @param completion receives {@code true} if confirmed
     */
    public static void confirm(
            DialogTexts texts,
            String message,
            Consumer<Boolean> completion
    ) {
        Dialog dialog = new Dialog();
        dialog.setModal(true);
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        if (texts != null && texts.title != null) {
            dialog.setHeaderTitle(texts.title);
        }

        dialog.add(message);

        Button cancel = new Button(
                texts != null ? texts.cancelText : "Cancel",
                e -> {
                    dialog.close();
                    completion.accept(false);
                }
        );

        Button ok = new Button(
                texts != null ? texts.okText : "OK",
                e -> {
                    dialog.close();
                    completion.accept(true);
                }
        );

        dialog.getFooter().add(cancel, ok);
        dialog.open();
    }

    // =====================================================================
    // FORM DIALOG
    // =====================================================================

    /**
     * Opens a dialog containing a {@link Form}.
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
     *              f.label("Name");
     *              f.validate(b -> b.asRequired("Required"));
     *          })
     *          .build();
     *
     * Dialogs.form(form, DialogTexts.of("Edit user", "Save", "Cancel"))
     *        .onOk(f -> save(f.binder().getBean()))
     *        .open();
     * }</pre>
     */
    public static <T> FormDialog<T> form(Form<T> form) {
        return new FormDialog<>(form, null);
    }

    /**
     * Same as {@link #form(Form)} but with dialog texts.
     */
    public static <T> FormDialog<T> form(Form<T> form, DialogTexts texts) {
        return new FormDialog<>(form, texts);
    }

    public static final class FormDialog<T> {

        private final Dialog dialog = new Dialog();
        private final Form<T> form;

        private BiConsumer<Form<T>, Dialog> onOk;
        private Runnable onCancel;

        private FormDialog(Form<T> form, DialogTexts texts) {
            this.form = form;

            dialog.add(form.layout());

            String okText = texts != null ? texts.okText : "OK";
            String cancelText = texts != null ? texts.cancelText : "Cancel";

            Button ok = Buttons.create()
                    .label(okText)
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

            Button cancel = new Button(cancelText);

            cancel.addClickListener(e -> {
                if (onCancel != null) {
                    onCancel.run();
                }
                dialog.close();
            });

            HorizontalLayout buttons = new HorizontalLayout(cancel, ok);
            dialog.add(buttons);
        }

        public FormDialog<T> onOk(Consumer<Form<T>> handler) {
            this.onOk = (f, d) -> {
                handler.accept(f);
                d.close();
            };
            return this;
        }

        public FormDialog<T> onOk(BiConsumer<Form<T>, Dialog> handler) {
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
    }


    // =====================================================================
    // SUPPORT TYPES
    // =====================================================================

    /**
     * Immutable container for dialog texts.
     * <p>
     * Intended as a lightweight placeholder until a proper i18n abstraction
     * is introduced.
     */
    public static final class DialogTexts {
        public final String title;
        public final String okText;
        public final String cancelText;

        private DialogTexts(String title, String okText, String cancelText) {
            this.title = title;
            this.okText = okText;
            this.cancelText = cancelText;
        }

        /**
         * Creates a {@link DialogTexts} instance.
         *
         * @param title      dialog title
         * @param okText     label for the confirmation button
         * @param cancelText label for the cancel button
         * @return texts container
         */
        public static DialogTexts of(String title, String okText, String cancelText) {
            return new DialogTexts(title, okText, cancelText);
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
