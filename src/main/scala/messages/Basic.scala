package messages

case class Basic(timestamp: String, host: String, plugin: String, plugin_instance: String,
                 `type`: String, type_instance: String, collectd_type: String, value: Float)
