listView('Try-something') {
    jobs {
        regex("try.*")
    }
    columns {
        status()
        weather()
        name()
        lastSuccess()
        lastFailure()
        lastDuration()
        buildButton()
    }
}

nestedView('parent-view'){
  views(){
    listView('Sub-View') {
      jobs {
        regex("example.*")
        
      }
      columns {
          status()
          weather()
          name()
          lastSuccess()
          lastFailure()
          lastDuration()
          buildButton()
      }
    }
  }
}



