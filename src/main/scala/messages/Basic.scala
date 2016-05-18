package messages

case class Basic(timestamp: String, host: String, plugin: String, plugin_instance: Option[String],
                 `type`: String, type_instance: Option[String], collectd_type: String, value: Float)
