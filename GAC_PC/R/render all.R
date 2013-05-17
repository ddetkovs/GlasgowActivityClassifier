renderAll <-function(){
  sidebysideplot <- grid.arrange(plotAll(testRes2, "standing", 0, 300), plotAll(testRes2, "sitting",0, 300),
  #                               #plotOne(testRes2, "sitting", 3,5, 300), plotOne(testRes2, "sitting", 4,6, 300),
                                plotAll(testRes2, "slow_walking", 0, 700), plotAll(testRes2, "fast_walking", 800,500),
  #                               plotOne(testRes2, "slow_walking", 3,5, 0, 700), plotOne(testRes2, "slow_walking", 4,6, 0,700),
  #                               plotOne(testRes2, "fast_walking", 3,5, 800, 500), plotOne(testRes2, "fast_walking", 4,6, 800, 500),
                                ncol=2)
#   sidebysideplot <- grid.arrange(plotAll(testRes2, "slow_running", 0, 300), plotAll(testRes2, "fast_running", 0, 300),
#                                  plotAll(testRes2, "upstairs", 75, 700), plotAll(testRes2, "downstairs", 0, 500), ncol=2)
  
}
renderAll()