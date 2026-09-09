package com.example.localization

object Strings {
    private val translations = mapOf(
        // General & Navigation
        "app_title" to mapOf(
            AppLanguage.ARABIC to "بطولات eFootball",
            AppLanguage.ENGLISH to "eFootball Tournaments",
            AppLanguage.FRENCH to "Tournois eFootball"
        ),
        "app_subtitle" to mapOf(
            AppLanguage.ARABIC to "منصة إدارة وتنظيم بطولات eFootball الرسمية",
            AppLanguage.ENGLISH to "Official eFootball Tournament & Match Organizer",
            AppLanguage.FRENCH to "Plateforme officielle d'organisation de tournois eFootball"
        ),
        "nav_home" to mapOf(
            AppLanguage.ARABIC to "الرئيسية",
            AppLanguage.ENGLISH to "Home",
            AppLanguage.FRENCH to "Accueil"
        ),
        "nav_tournaments" to mapOf(
            AppLanguage.ARABIC to "البطولات",
            AppLanguage.ENGLISH to "Tournaments",
            AppLanguage.FRENCH to "Tournois"
        ),
        "nav_matches" to mapOf(
            AppLanguage.ARABIC to "المباريات",
            AppLanguage.ENGLISH to "Matches",
            AppLanguage.FRENCH to "Matchs"
        ),
        "nav_standings" to mapOf(
            AppLanguage.ARABIC to "الترتيب",
            AppLanguage.ENGLISH to "Standings",
            AppLanguage.FRENCH to "Classement"
        ),
        "nav_players" to mapOf(
            AppLanguage.ARABIC to "اللاعبين",
            AppLanguage.ENGLISH to "Players",
            AppLanguage.FRENCH to "Joueurs"
        ),
        "nav_chat" to mapOf(
            AppLanguage.ARABIC to "الدردشة",
            AppLanguage.ENGLISH to "Chat",
            AppLanguage.FRENCH to "Discussion"
        ),
        "nav_admin" to mapOf(
            AppLanguage.ARABIC to "لوحة الإدارة",
            AppLanguage.ENGLISH to "Admin",
            AppLanguage.FRENCH to "Admin"
        ),
        "nav_profile" to mapOf(
            AppLanguage.ARABIC to "الملف الشخصي",
            AppLanguage.ENGLISH to "Profile",
            AppLanguage.FRENCH to "Profil"
        ),

        // Disclaimer
        "disclaimer_text" to mapOf(
            AppLanguage.ARABIC to "ملاحظة هامة: هذا التطبيق منصة تنظيمية لتنسيق البطولات وإدارة النتائج. تُلعب المباريات فعليًا داخل لعبة eFootball بين اللاعبين.",
            AppLanguage.ENGLISH to "Important Notice: This app is an intermediary platform to organize tournaments and manage results. Matches are played directly inside the eFootball game.",
            AppLanguage.FRENCH to "Note importante: Cette application organise les tournois et les résultats. Les matchs se jouent directement dans le jeu eFootball."
        ),

        // Auth
        "login" to mapOf(
            AppLanguage.ARABIC to "تسجيل الدخول",
            AppLanguage.ENGLISH to "Login",
            AppLanguage.FRENCH to "Connexion"
        ),
        "register" to mapOf(
            AppLanguage.ARABIC to "إنشاء حساب جديد",
            AppLanguage.ENGLISH to "Create Account",
            AppLanguage.FRENCH to "Créer un compte"
        ),
        "email" to mapOf(
            AppLanguage.ARABIC to "البريد الإلكتروني",
            AppLanguage.ENGLISH to "Email Address",
            AppLanguage.FRENCH to "Adresse Email"
        ),
        "password" to mapOf(
            AppLanguage.ARABIC to "كلمة المرور",
            AppLanguage.ENGLISH to "Password",
            AppLanguage.FRENCH to "Mot de passe"
        ),
        "player_name" to mapOf(
            AppLanguage.ARABIC to "اسم اللاعب",
            AppLanguage.ENGLISH to "Player Name",
            AppLanguage.FRENCH to "Nom du joueur"
        ),
        "efootball_id" to mapOf(
            AppLanguage.ARABIC to "معرّف eFootball ID",
            AppLanguage.ENGLISH to "eFootball ID",
            AppLanguage.FRENCH to "Identifiant eFootball ID"
        ),
        "efootball_id_hint" to mapOf(
            AppLanguage.ARABIC to "مثال: 948-120-435",
            AppLanguage.ENGLISH to "e.g., 948-120-435",
            AppLanguage.FRENCH to "ex: 948-120-435"
        ),
        "platform" to mapOf(
            AppLanguage.ARABIC to "المنصة الأساسية",
            AppLanguage.ENGLISH to "Gaming Platform",
            AppLanguage.FRENCH to "Plateforme"
        ),
        "platform_mobile" to mapOf(
            AppLanguage.ARABIC to "هاتف (Android / iOS)",
            AppLanguage.ENGLISH to "Mobile (Android / iOS)",
            AppLanguage.FRENCH to "Mobile (Android / iOS)"
        ),
        "platform_ps5" to mapOf(
            AppLanguage.ARABIC to "PlayStation 5",
            AppLanguage.ENGLISH to "PlayStation 5",
            AppLanguage.FRENCH to "PlayStation 5"
        ),
        "platform_xbox" to mapOf(
            AppLanguage.ARABIC to "Xbox Series X/S",
            AppLanguage.ENGLISH to "Xbox Series X/S",
            AppLanguage.FRENCH to "Xbox Series X/S"
        ),
        "platform_pc" to mapOf(
            AppLanguage.ARABIC to "كمبيوتر (PC / Steam)",
            AppLanguage.ENGLISH to "PC / Steam",
            AppLanguage.FRENCH to "PC / Steam"
        ),
        "select_avatar" to mapOf(
            AppLanguage.ARABIC to "اختر الصورة الرمزية",
            AppLanguage.ENGLISH to "Select Avatar",
            AppLanguage.FRENCH to "Choisir un avatar"
        ),
        "already_have_account" to mapOf(
            AppLanguage.ARABIC to "لديك حساب بالفعل؟ تسجيل الدخول",
            AppLanguage.ENGLISH to "Already have an account? Log In",
            AppLanguage.FRENCH to "Déjà un compte ? Connectez-vous"
        ),
        "dont_have_account" to mapOf(
            AppLanguage.ARABIC to "ليس لديك حساب؟ سجّل الآن",
            AppLanguage.ENGLISH to "Don't have an account? Sign Up",
            AppLanguage.FRENCH to "Pas de compte ? Inscrivez-vous"
        ),
        "logout" to mapOf(
            AppLanguage.ARABIC to "تسجيل الخروج",
            AppLanguage.ENGLISH to "Log Out",
            AppLanguage.FRENCH to "Déconnexion"
        ),
        "organizer_badge" to mapOf(
            AppLanguage.ARABIC to "منظّم البطولة (Admin)",
            AppLanguage.ENGLISH to "Organizer (Admin)",
            AppLanguage.FRENCH to "Organisateur (Admin)"
        ),
        "player_badge" to mapOf(
            AppLanguage.ARABIC to "لاعب مشارك",
            AppLanguage.ENGLISH to "Competitor",
            AppLanguage.FRENCH to "Joueur"
        ),

        // Home
        "stats_tournaments" to mapOf(
            AppLanguage.ARABIC to "البطولات",
            AppLanguage.ENGLISH to "Tournaments",
            AppLanguage.FRENCH to "Tournois"
        ),
        "stats_active_tournaments" to mapOf(
            AppLanguage.ARABIC to "البطولات النشطة",
            AppLanguage.ENGLISH to "Active Tournaments",
            AppLanguage.FRENCH to "Tournois Actifs"
        ),
        "stats_players" to mapOf(
            AppLanguage.ARABIC to "اللاعبين المسجلين",
            AppLanguage.ENGLISH to "Total Players",
            AppLanguage.FRENCH to "Total Joueurs"
        ),
        "stats_matches" to mapOf(
            AppLanguage.ARABIC to "المباريات المكتملة",
            AppLanguage.ENGLISH to "Matches Played",
            AppLanguage.FRENCH to "Matchs Joués"
        ),
        "featured_tournaments" to mapOf(
            AppLanguage.ARABIC to "البطولات المتاحة والنشطة",
            AppLanguage.ENGLISH to "Featured Tournaments",
            AppLanguage.FRENCH to "Tournois en Vedette"
        ),
        "recent_matches" to mapOf(
            AppLanguage.ARABIC to "آخر المباريات",
            AppLanguage.ENGLISH to "Recent Matches",
            AppLanguage.FRENCH to "Derniers Matchs"
        ),
        "create_tournament" to mapOf(
            AppLanguage.ARABIC to "إنشاء بطولة",
            AppLanguage.ENGLISH to "Create Tournament",
            AppLanguage.FRENCH to "Créer un tournoi"
        ),

        // Tournament Details & Creation
        "tournament_name" to mapOf(
            AppLanguage.ARABIC to "اسم البطولة",
            AppLanguage.ENGLISH to "Tournament Name",
            AppLanguage.FRENCH to "Nom du tournoi"
        ),
        "tournament_format" to mapOf(
            AppLanguage.ARABIC to "نظام البطولة",
            AppLanguage.ENGLISH to "Tournament Format",
            AppLanguage.FRENCH to "Format du tournoi"
        ),
        "format_knockout" to mapOf(
            AppLanguage.ARABIC to "خروج المغلوب (Knockout)",
            AppLanguage.ENGLISH to "Single Elimination (Knockout)",
            AppLanguage.FRENCH to "Élimination directe"
        ),
        "format_groups" to mapOf(
            AppLanguage.ARABIC to "مجموعات وتصفيات (Groups + Knockout)",
            AppLanguage.ENGLISH to "Groups + Knockout",
            AppLanguage.FRENCH to "Groupes + Élimination"
        ),
        "format_league" to mapOf(
            AppLanguage.ARABIC to "دوري النقاط (Round Robin)",
            AppLanguage.ENGLISH to "Round Robin League",
            AppLanguage.FRENCH to "Championnat (Round Robin)"
        ),
        "max_players" to mapOf(
            AppLanguage.ARABIC to "عدد اللاعبين",
            AppLanguage.ENGLISH to "Max Players",
            AppLanguage.FRENCH to "Nombre de joueurs"
        ),
        "start_date" to mapOf(
            AppLanguage.ARABIC to "تاريخ ووقت البداية",
            AppLanguage.ENGLISH to "Start Date & Time",
            AppLanguage.FRENCH to "Date et heure de début"
        ),
        "prizes" to mapOf(
            AppLanguage.ARABIC to "الجوائز والمكافآت",
            AppLanguage.ENGLISH to "Prizes & Rewards",
            AppLanguage.FRENCH to "Prix et Récompenses"
        ),
        "rules" to mapOf(
            AppLanguage.ARABIC to "القوانين والتعليمات",
            AppLanguage.ENGLISH to "Rules & Instructions",
            AppLanguage.FRENCH to "Règles et Instructions"
        ),
        "status_registration_open" to mapOf(
            AppLanguage.ARABIC to "التسجيل مفتوح",
            AppLanguage.ENGLISH to "Registration Open",
            AppLanguage.FRENCH to "Inscriptions ouvertes"
        ),
        "status_started" to mapOf(
            AppLanguage.ARABIC to "بدأت البطولة",
            AppLanguage.ENGLISH to "Started",
            AppLanguage.FRENCH to "Démarré"
        ),
        "status_in_progress" to mapOf(
            AppLanguage.ARABIC to "جارية الآن",
            AppLanguage.ENGLISH to "In Progress",
            AppLanguage.FRENCH to "En cours"
        ),
        "status_completed" to mapOf(
            AppLanguage.ARABIC to "انتهت البطولة",
            AppLanguage.ENGLISH to "Completed",
            AppLanguage.FRENCH to "Terminé"
        ),
        "join_tournament" to mapOf(
            AppLanguage.ARABIC to "انضمام للبطولة",
            AppLanguage.ENGLISH to "Join Tournament",
            AppLanguage.FRENCH to "Rejoindre le tournoi"
        ),
        "already_joined" to mapOf(
            AppLanguage.ARABIC to "أنت منضم بالفعل ✓",
            AppLanguage.ENGLISH to "Already Joined ✓",
            AppLanguage.FRENCH to "Déjà inscrit ✓"
        ),
        "tournament_full" to mapOf(
            AppLanguage.ARABIC to "اكتمل العدد",
            AppLanguage.ENGLISH to "Tournament Full",
            AppLanguage.FRENCH to "Tournoi Complet"
        ),
        "participants" to mapOf(
            AppLanguage.ARABIC to "قائمة اللاعبين المشاركين",
            AppLanguage.ENGLISH to "Participants Roster",
            AppLanguage.FRENCH to "Liste des participants"
        ),
        "start_tournament_now" to mapOf(
            AppLanguage.ARABIC to "بدء البطولة وتوليد المباريات",
            AppLanguage.ENGLISH to "Start Tournament & Generate Brackets",
            AppLanguage.FRENCH to "Démarrer et générer les matchs"
        ),

        // Matches & Ready System
        "round_1" to mapOf(
            AppLanguage.ARABIC to "الجولة الأولى",
            AppLanguage.ENGLISH to "Round 1",
            AppLanguage.FRENCH to "1er Tour"
        ),
        "quarter_finals" to mapOf(
            AppLanguage.ARABIC to "ربع النهائي",
            AppLanguage.ENGLISH to "Quarter-Finals",
            AppLanguage.FRENCH to "Quarts de finale"
        ),
        "semi_finals" to mapOf(
            AppLanguage.ARABIC to "نصف النهائي",
            AppLanguage.ENGLISH to "Semi-Finals",
            AppLanguage.FRENCH to "Demi-finales"
        ),
        "final_round" to mapOf(
            AppLanguage.ARABIC to "المباراة النهائية 🏆",
            AppLanguage.ENGLISH to "Final 🏆",
            AppLanguage.FRENCH to "Finale 🏆"
        ),
        "match_status_scheduled" to mapOf(
            AppLanguage.ARABIC to "مجدولة",
            AppLanguage.ENGLISH to "Scheduled",
            AppLanguage.FRENCH to "Programmée"
        ),
        "match_status_waiting_ready" to mapOf(
            AppLanguage.ARABIC to "في انتظار الجاهزية",
            AppLanguage.ENGLISH to "Waiting for Ready",
            AppLanguage.FRENCH to "En attente de confirmation"
        ),
        "match_status_live" to mapOf(
            AppLanguage.ARABIC to "المباراة جارية داخل eFootball",
            AppLanguage.ENGLISH to "Match Active in eFootball",
            AppLanguage.FRENCH to "Match en cours dans eFootball"
        ),
        "match_status_under_review" to mapOf(
            AppLanguage.ARABIC to "النتيجة قيد مراجعة المنظم",
            AppLanguage.ENGLISH to "Under Organizer Review",
            AppLanguage.FRENCH to "En cours d'examen"
        ),
        "match_status_completed" to mapOf(
            AppLanguage.ARABIC to "انتهت واعتمدت النتيجة",
            AppLanguage.ENGLISH to "Result Confirmed",
            AppLanguage.FRENCH to "Résultat Confirmé"
        ),
        "btn_ready" to mapOf(
            AppLanguage.ARABIC to "أنا جاهز للمباراة",
            AppLanguage.ENGLISH to "I'm Ready",
            AppLanguage.FRENCH to "Je suis prêt"
        ),
        "btn_you_are_ready" to mapOf(
            AppLanguage.ARABIC to "أنت جاهز ✓",
            AppLanguage.ENGLISH to "You're Ready ✓",
            AppLanguage.FRENCH to "Vous êtes prêt ✓"
        ),
        "btn_waiting_opponent" to mapOf(
            AppLanguage.ARABIC to "في انتظار الخصم...",
            AppLanguage.ENGLISH to "Waiting for opponent...",
            AppLanguage.FRENCH to "En attente de l'adversaire..."
        ),
        "both_ready_msg" to mapOf(
            AppLanguage.ARABIC to "كلا اللاعبين جاهزان! افتحا لعبة eFootball، أضيفا بعضكما بواسطة المعرّف وابدآ المباراة.",
            AppLanguage.ENGLISH to "Both players are ready! Open eFootball, add each other via ID, and start your match.",
            AppLanguage.FRENCH to "Les deux joueurs sont prêts ! Ouvrez eFootball, ajoutez-vous par ID et lancez le match."
        ),
        "submit_result" to mapOf(
            AppLanguage.ARABIC to "إرسال النتيجة",
            AppLanguage.ENGLISH to "Submit Score",
            AppLanguage.FRENCH to "Envoyer le score"
        ),
        "proof_screenshot" to mapOf(
            AppLanguage.ARABIC to "صورة إثبات النتيجة من اللعبة",
            AppLanguage.ENGLISH to "Result Screenshot Proof",
            AppLanguage.FRENCH to "Preuve Capture d'écran"
        ),
        "upload_proof" to mapOf(
            AppLanguage.ARABIC to "إرفاق صورة الإثبات",
            AppLanguage.ENGLISH to "Attach Screenshot",
            AppLanguage.FRENCH to "Joindre capture d'écran"
        ),
        "proof_attached" to mapOf(
            AppLanguage.ARABIC to "تم إرفاق صورة الإثبات بنجاح ✓",
            AppLanguage.ENGLISH to "Screenshot Attached Successfully ✓",
            AppLanguage.FRENCH to "Capture jointe avec succès ✓"
        ),
        "copy_id" to mapOf(
            AppLanguage.ARABIC to "نسخ المعرّف",
            AppLanguage.ENGLISH to "Copy ID",
            AppLanguage.FRENCH to "Copier l'ID"
        ),
        "id_copied" to mapOf(
            AppLanguage.ARABIC to "تم نسخ المعرّف إلى الحافظة!",
            AppLanguage.ENGLISH to "eFootball ID copied to clipboard!",
            AppLanguage.FRENCH to "ID eFootball copié !"
        ),

        // Admin
        "admin_title" to mapOf(
            AppLanguage.ARABIC to "لوحة تحكم المنظم (Admin)",
            AppLanguage.ENGLISH to "Organizer Control Panel",
            AppLanguage.FRENCH to "Panneau de Contrôle Admin"
        ),
        "pending_scores" to mapOf(
            AppLanguage.ARABIC to "النتائج المعلقة للمراجعة والاعتماد",
            AppLanguage.ENGLISH to "Pending Scores for Review",
            AppLanguage.FRENCH to "Scores en attente de révision"
        ),
        "accept_result" to mapOf(
            AppLanguage.ARABIC to "قبول النتيجة واعتامدها",
            AppLanguage.ENGLISH to "Accept & Confirm Score",
            AppLanguage.FRENCH to "Accepter et Confirmer"
        ),
        "reject_result" to mapOf(
            AppLanguage.ARABIC to "رفض النتيجة",
            AppLanguage.ENGLISH to "Reject Score",
            AppLanguage.FRENCH to "Rejeter le score"
        ),
        "manage_players" to mapOf(
            AppLanguage.ARABIC to "إدارة المشاركين",
            AppLanguage.ENGLISH to "Manage Participants",
            AppLanguage.FRENCH to "Gérer les participants"
        ),
        "remove_player" to mapOf(
            AppLanguage.ARABIC to "استبعاد اللاعب",
            AppLanguage.ENGLISH to "Disqualify / Remove",
            AppLanguage.FRENCH to "Exclure"
        ),
        "no_pending_scores" to mapOf(
            AppLanguage.ARABIC to "لا توجد نتائج تنتظر المراجعة حاليًا",
            AppLanguage.ENGLISH to "No pending score submissions right now.",
            AppLanguage.FRENCH to "Aucun résultat en attente pour le moment."
        ),

        // Standings
        "table_rank" to mapOf(
            AppLanguage.ARABIC to "#",
            AppLanguage.ENGLISH to "#",
            AppLanguage.FRENCH to "#"
        ),
        "table_player" to mapOf(
            AppLanguage.ARABIC to "اللاعب",
            AppLanguage.ENGLISH to "Player",
            AppLanguage.FRENCH to "Joueur"
        ),
        "table_played" to mapOf(
            AppLanguage.ARABIC to "لعب",
            AppLanguage.ENGLISH to "P",
            AppLanguage.FRENCH to "J"
        ),
        "table_won" to mapOf(
            AppLanguage.ARABIC to "فوز",
            AppLanguage.ENGLISH to "W",
            AppLanguage.FRENCH to "V"
        ),
        "table_drawn" to mapOf(
            AppLanguage.ARABIC to "تعادل",
            AppLanguage.ENGLISH to "D",
            AppLanguage.FRENCH to "N"
        ),
        "table_lost" to mapOf(
            AppLanguage.ARABIC to "خسارة",
            AppLanguage.ENGLISH to "L",
            AppLanguage.FRENCH to "D"
        ),
        "table_goals" to mapOf(
            AppLanguage.ARABIC to "+/-",
            AppLanguage.ENGLISH to "GD",
            AppLanguage.FRENCH to "Diff"
        ),
        "table_points" to mapOf(
            AppLanguage.ARABIC to "النقاط",
            AppLanguage.ENGLISH to "PTS",
            AppLanguage.FRENCH to "PTS"
        ),

        // Chat
        "chat_placeholder" to mapOf(
            AppLanguage.ARABIC to "اكتب رسالة للغرفة...",
            AppLanguage.ENGLISH to "Type a message...",
            AppLanguage.FRENCH to "Écrivez un message..."
        ),
        "chat_send" to mapOf(
            AppLanguage.ARABIC to "إرسال",
            AppLanguage.ENGLISH to "Send",
            AppLanguage.FRENCH to "Envoyer"
        ),
        "chat_general_room" to mapOf(
            AppLanguage.ARABIC to "غرفة المحادثة العامة للبطولة",
            AppLanguage.ENGLISH to "General Tournament Chat Room",
            AppLanguage.FRENCH to "Salon général du tournoi"
        ),

        // Settings / Profile
        "language_select" to mapOf(
            AppLanguage.ARABIC to "لغة التطبيق (Language)",
            AppLanguage.ENGLISH to "App Language",
            AppLanguage.FRENCH to "Langue de l'application"
        ),
        "firebase_connection" to mapOf(
            AppLanguage.ARABIC to "حالة المزامنة السحابية (Firebase)",
            AppLanguage.ENGLISH to "Cloud Sync Status (Firebase)",
            AppLanguage.FRENCH to "Statut de synchronisation (Firebase)"
        ),
        "firebase_status_connected" to mapOf(
            AppLanguage.ARABIC to "متصل بالسحابة (Firebase Realtime DB Online)",
            AppLanguage.ENGLISH to "Connected (Firebase Realtime DB Online)",
            AppLanguage.FRENCH to "Connecté (Firebase Realtime DB En ligne)"
        ),
        "firebase_status_local" to mapOf(
            AppLanguage.ARABIC to "تخزين محلي فوري (Room Database Active)",
            AppLanguage.ENGLISH to "Local Offline Storage Active (Room)",
            AppLanguage.FRENCH to "Stockage Local Actif (Room)"
        ),
        "save_changes" to mapOf(
            AppLanguage.ARABIC to "حفظ التعديلات",
            AppLanguage.ENGLISH to "Save Changes",
            AppLanguage.FRENCH to "Enregistrer"
        ),
        "close" to mapOf(
            AppLanguage.ARABIC to "إغلاق",
            AppLanguage.ENGLISH to "Close",
            AppLanguage.FRENCH to "Fermer"
        ),
        "cancel" to mapOf(
            AppLanguage.ARABIC to "إلغاء",
            AppLanguage.ENGLISH to "Cancel",
            AppLanguage.FRENCH to "Annuler"
        ),
        "all" to mapOf(
            AppLanguage.ARABIC to "الكل",
            AppLanguage.ENGLISH to "All",
            AppLanguage.FRENCH to "Tous"
        ),
        "search_players" to mapOf(
            AppLanguage.ARABIC to "بحث عن لاعب أو معرّف eFootball...",
            AppLanguage.ENGLISH to "Search player or eFootball ID...",
            AppLanguage.FRENCH to "Rechercher joueur ou ID eFootball..."
        ),
        "vs" to mapOf(
            AppLanguage.ARABIC to "ضد",
            AppLanguage.ENGLISH to "VS",
            AppLanguage.FRENCH to "VS"
        )
    )

    fun get(key: String, language: AppLanguage): String {
        return translations[key]?.get(language) ?: translations[key]?.get(AppLanguage.ARABIC) ?: key
    }
}
