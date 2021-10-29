package de.tforneberg.patchdb.model

class Views {
    interface BriefView
    interface DefaultView : BriefView
    interface CompleteView : DefaultView
}