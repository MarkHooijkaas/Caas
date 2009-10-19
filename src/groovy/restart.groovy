println "loading restart"
// Shows runtime information about all SoapProcessors
// It is named after the ps command in Unix

restart = { sys, name ->
  for (sp in sys.sp.like(name)) { 
    if (sp.status=="Started") {
	  println "Restarting ${sp}"
	  sp.restart()
	}
  }
}