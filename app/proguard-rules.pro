# ==============================================================
# Daily Soul Journal – ProGuard Rules
# ==============================================================
# Keep Room entity classes (reflection-based at runtime)
-keep class com.dailysoul.journal.data.model.** { *; }

# Keep DAO interfaces
-keep class com.dailysoul.journal.data.db.** { *; }

# Keep ViewBinding generated classes
-keep class com.dailysoul.journal.databinding.** { *; }

# Keep Gson for JSON serialization
-keep class com.google.gson.** { *; }
