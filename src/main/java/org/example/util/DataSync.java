package org.example.util;

import org.example.data.Supermarket;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DataSync {

    private static boolean autoSaveEnabled = true;

    // One background thread, so saves upload in the order they were made
    // and an older save can never overwrite a newer one. The thread is not a
    // daemon, so a pending save still finishes when the window closes; it
    // stops after 5 idle seconds so the app can exit.
    private static final ExecutorService SAVE_EXECUTOR = createSaveExecutor();

    private static ExecutorService createSaveExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                1, 1, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
                runnable -> new Thread(runnable, "sheets-autosave"));
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    public static void save(Supermarket market) {
        if (!autoSaveEnabled || market == null) return;

        // Snapshot the data now, on the calling (UI) thread, so the background
        // upload never reads the lists while the UI is changing them.
        String marketName = market.getName();
        List<List<Object>> rows = GoogleSheetsService.buildRows(market);

        SAVE_EXECUTOR.submit(() -> {
            try {
                GoogleSheetsService.writeRows(marketName, rows);
                System.out.println("💾 Auto-saved to Google Sheets.");
            } catch (Exception e) {
                System.err.println("⚠️ Auto-save failed: " + e.getMessage());
            }
        });
    }

    public static void setAutoSaveEnabled(boolean enabled) {
        autoSaveEnabled = enabled;
    }
}
