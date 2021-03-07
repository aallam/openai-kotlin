interface Library {

  val group: String
  val artifact: String
  val version: String

  operator fun invoke(module: String? = null): String {
    val optionalModule = if (module != null) "-$module" else ""
    return "$group:$artifact$optionalModule:$version"
  }
}
