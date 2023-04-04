package com.example.appause

enum class AppCategory {
    OTHER,
    ART_AND_DESIGN,
    AUTO_AND_VEHICLES,
    BEAUTY,
    BOOKS_AND_REFERENCE,
    BUSINESS,
    COMICS,
    COMMUNICATION,
    DATING,
    EDUCATION,
    ENTERTAINMENT,
    EVENTS,
    FINANCE,
    FOOD_AND_DRINK,
    HEALTH_AND_FITNESS,
    HOUSE_AND_HOME,
    LIBRARIES_AND_DEMO,
    LIFESTYLE,
    MAPS_AND_NAVIGATION,
    MEDICAL,
    MUSIC_AND_AUDIO,
    NEWS_AND_MAGAZINES,
    PARENTING,
    PERSONALIZATION,
    PHOTOGRAPHY,
    PRODUCTIVITY,
    SHOPPING,
    SOCIAL,
    SPORTS,
    TOOLS,
    TRAVEL_AND_LOCAL,
    VIDEO_PLAYERS,
    WEATHER,
    GAMES;

    companion object {
        private val map = values().associateBy(AppCategory::name)
        private const val CATEGORY_GAME_STRING = "GAME_" // All games start with this prefix

        fun fromCategoryName(name: String): AppCategory {
            if (name.contains(CATEGORY_GAME_STRING)) return GAMES
            return map[name.uppercase()] ?: OTHER
        }
    }
}