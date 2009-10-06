import org.kisst.cordys.caas.Caas
cordys=Caas.connect("local/c2.properties")
//println cordys.children
for (i in cordys.isvps) {
  println "" + i +" ==> " + i.dn
  for (ms in i.methodSets) {
    println "    " +ms
    for (m in ms.methods)
      println "        " +m
  }
  for (r in i.roles)
    println "    " +r
} 
for (o in cordys.organizations) { 
  println "" + o + " ==> " + o.dn
  for (u in o.users)
    println "    " +u
  for (ms in o.methodSets) {
    println "    " +ms
    for (m in ms.methods)
      println "        " +m
  }
  for (r in o.roles)
    println "    " +r
  for (s in o.soapNodes) {
    println "    " +s
    for (p in s.soapProcessors)
      println "        " +p
  }
}

