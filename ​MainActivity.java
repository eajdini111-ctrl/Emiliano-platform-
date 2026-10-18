package com.example.emilianoplatform;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class MainActivity extends Activity {

    private LinearLayout root;
    private LinearLayout topBar;
    private LinearLayout bottomBar;
    private LinearLayout content;
    private EditText editor;
    private TextView projectTitle;
    private TextView pageTitle;

    private String projectName = "Projekti Pa Emër";
    private String currentFile = "MainActivity.java";
    private String currentPage = "Baza";

    private final List<String> files = new ArrayList<String>();
    private final Stack<String> undoStack = new Stack<String>();

    private android.content.SharedPreferences prefs;

    private static final String PREFS = "EmilianoProjectsPrefs";

    private int dp(float value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        projectName = prefs.getString(
                "LAST_PROJECT",
                "Projekti Pa Emër"
        );

        loadFiles();
        buildInterface();
        showBazaView();
    }

    private void loadFiles() {
        files.clear();

        files.add("MainActivity.java");
        files.add("AndroidManifest.xml");
        files.add("strings.xml");

        for (String file : files) {
            String key = getFileKey(file);

            if (!prefs.contains(key)) {
                prefs.edit().putString(
                        key,
                        getDefaultContent(file)
                ).apply();
            }
        }
    }

    private String getFileKey(String file) {
        return "PROJECT_" + projectName + "_" + file;
    }

    private String getDefaultContent(String file) {
        if ("MainActivity.java".equals(file)) {
            return "package com.example.app;\n\n"
                    + "import android.app.Activity;\n"
                    + "import android.os.Bundle;\n"
                    + "import android.widget.TextView;\n\n"
                    + "public class MainActivity extends Activity {\n"
                    + "    @Override\n"
                    + "    protected void onCreate(Bundle savedInstanceState) {\n"
                    + "        super.onCreate(savedInstanceState);\n"
                    + "        TextView text = new TextView(this);\n"
                    + "        text.setText(\"Aplikacioni im\");\n"
                    + "        text.setTextSize(24);\n"
                    + "        setContentView(text);\n"
                    + "    }\n"
                    + "}\n";
        }

        if ("AndroidManifest.xml".equals(file)) {
            return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                    + "<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\">\n"
                    + "    <application\n"
                    + "        android:theme=\"@android:style/Theme.Material.Light.NoActionBar\"\n"
                    + "        android:label=\"Aplikacioni im\">\n"
                    + "        <activity\n"
                    + "            android:name=\".MainActivity\"\n"
                    + "            android:exported=\"true\">\n"
                    + "            <intent-filter>\n"
                    + "                <action android:name=\"android.intent.action.MAIN\" />\n"
                    + "                <category android:name=\"android.intent.category.LAUNCHER\" />\n"
                    + "            </intent-filter>\n"
                    + "        </activity>\n"
                    + "    </application>\n"
                    + "</manifest>\n";
        }

        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                + "<resources>\n"
                + "    <string name=\"app_name\">Aplikacioni im</string>\n"
                + "</resources>\n";
    }

    private void buildInterface() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.BLACK);

        setContentView(root);

        topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.VERTICAL);
        topBar.setPadding(dp(8), dp(5), dp(8), dp(5));
        topBar.setBackgroundColor(Color.rgb(24, 24, 24));

        root.addView(topBar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        projectTitle = new TextView(this);
        projectTitle.setTextColor(Color.WHITE);
        projectTitle.setTextSize(16);
        projectTitle.setTypeface(null, Typeface.BOLD);
        projectTitle.setPadding(dp(5), dp(5), dp(5), dp(8));
        topBar.addView(projectTitle);

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        topBar.addView(actions);

        addButton(actions, "Meny", new Runnable() {
            @Override
            public void run() {
                showMainMenu();
            }
        });

        addButton(actions, "Undo", new Runnable() {
            @Override
            public void run() {
                undo();
            }
        });

        addButton(actions, "Save", new Runnable() {
            @Override
            public void run() {
                saveCurrentFile();
            }
        });

        addButton(actions, "Build Room", new Runnable() {
            @Override
            public void run() {
                prepareBuild();
            }
        });

        ScrollView scroll = new ScrollView(this);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(10), dp(10), dp(10), dp(10));
        scroll.addView(content);

        root.addView(scroll, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1
        ));

        bottomBar = new LinearLayout(this);
        bottomBar.setOrientation(LinearLayout.HORIZONTAL);
        bottomBar.setBackgroundColor(Color.rgb(24, 24, 24));

        root.addView(bottomBar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(55)
        ));

        addButton(bottomBar, "Baza", new Runnable() {
            @Override
            public void run() {
                showBazaView();
            }
        });

        addButton(bottomBar, "Skedarët", new Runnable() {
            @Override
            public void run() {
                showFilesView();
            }
        });

        addButton(bottomBar, "Konsola", new Runnable() {
            @Override
            public void run() {
                showConsole();
            }
        });

        addButton(bottomBar, "AI", new Runnable() {
            @Override
            public void run() {
                showAI();
            }
        });

        updateProjectTitle();
    }

    private void addButton(
            LinearLayout parent,
            String text,
            final Runnable action) {

        Button button = new Button(this);
        button.setText(text);
        button.setTextSize(11);
        button.setTextColor(Color.WHITE);
        button.setAllCaps(false);
        button.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.rgb(55, 55, 55)
                )
        );

        parent.addView(button, new LinearLayout.LayoutParams(
                0,
                dp(44),
                1
        ));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action.run();
            }
        });
    }

    private TextView makeLabel(String text, int size) {
        TextView label = new TextView(this);
        label.setText(text);
        label.setTextColor(Color.WHITE);
        label.setTextSize(size);
        label.setPadding(0, dp(8), 0, dp(8));
        return label;
    }

    private void updateProjectTitle() {
        if (projectTitle != null) {
            projectTitle.setText(
                    "Emiliano Platform  |  " + projectName
            );
        }
    }

    private void showPageTitle(String title) {
        content.removeAllViews();
        currentPage = title;
        content.addView(makeLabel(title, 20));
    }

    private void showBazaView() {
        showPageTitle("Baza");

        content.addView(makeLabel(
                "Skedari aktual: " + currentFile, 14
        ));

        for (final String file : files) {
            Button item = new Button(this);
            item.setText(file + "\nHap në editor");
            item.setAllCaps(false);
            item.setTextColor(Color.WHITE);
            item.setGravity(Gravity.CENTER_VERTICAL);
            item.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            Color.rgb(35, 35, 35)
                    )
            );

            content.addView(item, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp(58)
            ));

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openFile(file);
                }
            });
        }
    }

    private void showFilesView() {
        showPageTitle("Skedarët");

        content.addView(makeLabel(
                "Projekti: " + projectName, 14
        ));

        content.addView(makeLabel(
                "Struktura bazë e projektit", 13
        ));

        for (String folder : new String[]{
                "src", "res", "assets", "libs", "bin"
        }) {
            content.addView(makeLabel(
                    "📁 " + folder, 14
            ));
        }

        for (final String file : files) {
            Button item = new Button(this);
            item.setText("📄 " + file);
            item.setAllCaps(false);
            item.setTextColor(Color.WHITE);
            item.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            Color.rgb(40, 40, 40)
                    )
            );

            content.addView(item);

            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openFile(file);
                }
            });
        }
    }

    private void openFile(String file) {
        saveEditorText();

        if (!files.contains(file)) {
            Toast.makeText(
                    this,
                    "Skedari nuk ekziston.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        currentFile = file;
        undoStack.clear();

        String text = prefs.getString(
                getFileKey(file),
                getDefaultContent(file)
        );

        showEditor(text);
    }

    private void showEditor(String text) {
        showPageTitle("Editor: " + currentFile);

        content.addView(makeLabel(
                projectName + " / " + currentFile, 12
        ));

        editor = new EditText(this);
        editor.setTextColor(Color.WHITE);
        editor.setHintTextColor(Color.GRAY);
        editor.setHint("Shkruaj kodin këtu...");
        editor.setTextSize(13);
        editor.setTypeface(Typeface.MONOSPACE);
        editor.setGravity(Gravity.TOP | Gravity.START);
        editor.setPadding(dp(10), dp(10), dp(10), dp(10));
        editor.setMinLines(15);
        editor.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        Color.rgb(70, 70, 70)
                )
        );
        editor.setText(text);

        content.addView(editor, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(360)
        ));

        editor.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });

        LinearLayout tools = new LinearLayout(this);
        tools.setOrientation(LinearLayout.HORIZONTAL);
        content.addView(tools);

        addButton(tools, "Ruaj", new Runnable() {
            @Override
            public void run() {
                saveCurrentFile();
            }
        });

        addButton(tools, "Kopjo", new Runnable() {
            @Override
            public void run() {
                copyText();
            }
        });

        addButton(tools, "Ngjit", new Runnable() {
            @Override
            public void run() {
                pasteText();
            }
        });

        addButton(tools, "Zgjidh të gjitha", new Runnable() {
            @Override
            public void run() {
                if (editor != null) {
                    editor.selectAll();
                }
            }
        });
    }

    private void saveEditorText() {
        if (editor != null) {
            String text = editor.getText().toString();

            prefs.edit().putString(
                    getFileKey(currentFile),
                    text
            ).apply();
        }
    }

    private void saveCurrentFile() {
        if (editor != null) {
            undoStack.push(editor.getText().toString());
            saveEditorText();
        }

        prefs.edit().putString(
                "LAST_PROJECT",
                projectName
        ).apply();

        Toast.makeText(
                this,
                "Projekti u ruajt.",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void undo() {
        if (editor == null) {
            Toast.makeText(
                    this,
                    "Hap një skedar në editor fillimisht.",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (!undoStack.empty()) {
            editor.setText(undoStack.pop());
        } else {
            Toast.makeText(
                    this,
                    "Nuk ka veprime për Undo.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void copyText() {
        if (editor == null) {
            return;
        }

        ClipboardManager clipboard =
                (ClipboardManager) getSystemService(
                        Context.CLIPBOARD_SERVICE
                );

        clipboard.setPrimaryClip(ClipData.newPlainText(
                "Kodi",
                editor.getText().toString()
        ));

        Toast.makeText(
                this,
                "Kodi u kopjua.",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void pasteText() {
        if (editor == null) {
            return;
        }

        ClipboardManager clipboard =
                (ClipboardManager) getSystemService(
                        Context.CLIPBOARD_SERVICE
                );

        if (clipboard.hasPrimaryClip()
                && clipboard.getPrimaryClip() != null
                && clipboard.getPrimaryClip().getItemCount() > 0) {

            CharSequence pasted = clipboard.getPrimaryClip()
                    .getItemAt(0).coerceToText(this);

            int start = Math.max(
                    0,
                    editor.getSelectionStart()
            );

            editor.getText().insert(start, pasted);
        }
    }

    private void showMainMenu() {
        final String[] options = {
                "Projekti i ri",
                "Ruaj projektin",
                "Hap skedarët",
                "Informacion"
        };

        new AlertDialog.Builder(this)
                .setTitle("Menyja Kryesore")
                .setItems(options,
                        new android.content.DialogInterface
                                .OnClickListener() {
                            @Override
                            public void onClick(
                                    android.content.DialogInterface dialog,
                                    int which) {

                                if (which == 0) {
                                    createProjectDialog();
                                } else if (which == 1) {
                                    saveCurrentFile();
                                } else if (which == 2) {
                                    showFilesView();
                                } else {
                                    showInformation();
                                }
                            }
                        })
                .setNegativeButton("Mbyll", null)
                .show();
    }

    private void createProjectDialog() {
        final EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setHint("Emri i projektit");

        new AlertDialog.Builder(this)
                .setTitle("Projekti i ri")
                .setView(input)
                .setPositiveButton("Krijo",
                        new android.content.DialogInterface
                                .OnClickListener() {
                            @Override
                            public void onClick(
                                    android.content.DialogInterface dialog,
                                    int which) {

                                String name = input.getText()
                                        .toString().trim();

                                if (name.length() == 0) {
                                    name = "Projekti Pa Emër";
                                }

                                saveEditorText();

                                projectName = name;
                                currentFile = "MainActivity.java";

                                loadFiles();
                                updateProjectTitle();
                                showBazaView();

                                prefs.edit().putString(
                                        "LAST_PROJECT",
                                        projectName
                                ).apply();

                                Toast.makeText(
                                        MainActivity.this,
                                        "Projekti u krijua.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        })
                .setNegativeButton("Anulo", null)
                .show();
    }

    private void prepareBuild() {
        saveEditorText();

        prefs.edit().putString(
                "LAST_PROJECT",
                projectName
        ).apply();

        new AlertDialog.Builder(this)
                .setTitle("Build Room")
                .setMessage(
                        "Projekti u ruajt.\n\n"
                        + "Ky version nuk e kompilon ende "
                        + "projektin në një APK të instalueshme.\n\n"
                        + "Hapi tjetër është integrimi i "
                        + "motorit real të ndërtimit."
                )
                .setPositiveButton("Në rregull", null)
                .show();
    }

    private void showConsole() {
        showPageTitle("Konsola");

        content.addView(makeLabel(
                "Emiliano Platform", 16
        ));

        content.addView(makeLabel(
                "Projekti: " + projectName, 14
        ));

        content.addView(makeLabel(
                "Skedarë: " + files.size(), 14
        ));

        content.addView(makeLabel(
                "Gjendja: Gati për zhvillim", 14
        ));

        content.addView(makeLabel(
                "Ndërtimi real i APK-së nuk është "
                + "aktivizuar ende.", 13
        ));
    }

    private void showAI() {
        showPageTitle("AI");

        content.addView(makeLabel(
                "Asistenti AI do të integrohet në një fazë tjetër.",
                14
        ));

        content.addView(makeLabel(
                "Këtu do të shtojmë gjenerimin dhe "
                + "përmirësimin e kodit.", 13
        ));
    }

    private void showInformation() {
        new AlertDialog.Builder(this)
                .setTitle("Emiliano Platform")
                .setMessage(
                        "Platformë për krijimin dhe "
                        + "menaxhimin e projekteve Android.\n\n"
                        + "Versioni bazë."
                )
                .setPositiveButton("Mbyll", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Mbyll aplikacionin")
                .setMessage(
                        "Dëshiron ta mbyllësh aplikacionin?"
                )
                .setNegativeButton("Jo", null)
                .setPositiveButton("Po",
                        new android.content.DialogInterface
                                .OnClickListener() {
                            @Override
                            public void onClick(
                                    android.content.DialogInterface dialog,
                                    int which) {

                                saveEditorText();

                                prefs.edit().putString(
                                        "LAST_PROJECT",
                                        projectName
                                ).apply();

                                finish();
                            }
                        })
                .show();
    }
}
