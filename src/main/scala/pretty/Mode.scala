package io.github.torsteinvik.zetatypes.db.pretty

abstract sealed class PrettyMode
case object Plain extends PrettyMode
case object HTML extends PrettyMode
case object LaTeX extends PrettyMode
