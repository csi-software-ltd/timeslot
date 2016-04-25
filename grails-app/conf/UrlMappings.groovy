class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(controller : "index", action:"index")
		"/restore"(controller : "index", action:"restore")
		"/passrestore/$id?"(controller : "index", action:"passrestore")
		"/confirm/$id?"(controller : "index", action:"confirm")
		"/admin"(controller : "admin", action:"index")
    "/favicon.ico"(uri:"/favicon.ico")

		"/$linkname" {
			controller = "company"
			action = "index"
			id = { def t; Partner.withNewSession{ t = Partner.findByLinkname(params.linkname)}; t.id }
			constraints {
				linkname(validator:{ def t = true; def item = it; Partner.withNewSession{ t = Partner.findByLinkname(item)?true:false; }; return t})
			}
		}

		"/$linkname/$action" {
			controller = "company"
			id = { def t; Partner.withNewSession{ t = Partner.findByLinkname(params.linkname)}; t.id }
			constraints {
				linkname(validator:{ def t = true; def item = it; Partner.withNewSession{ t = Partner.findByLinkname(item)?true:false; }; return t})
			}
		}

		"500"(view:'/error')
		"401"(view:'/error_401')
		"403"(view:'/error_403')
	}
}