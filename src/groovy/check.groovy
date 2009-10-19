println "loading check"
check = { sys ->
  for (sp in sys.sp) { 
    if (! sp.useSystemLogPolicy) { println "${sp} does not log using the system policy"; }
    if (sp.status.startsWith("Config") || sp.status.startsWith("Unknown")) { 
      println "${sp}\t ${sp.status}"; 
      sp.restart();
    }
  }
}
