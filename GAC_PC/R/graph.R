# colnames(acc) <- c('activity', 'timestamp', 'horizontal_acc', 'vertical_acc')

plotAll <- function(data, activity, start, size){
  require(ggplot2)
  require(reshape)
  startTimestamp <- data$timestamp[data$activity == activity][1]+start
  dataSubSet <- data[data$activity == activity & data$timestamp > startTimestamp & data$timestamp < startTimestamp + size,]
  
  hor <- data.frame(time = dataSubSet$timestamp, 
                    hor_acc = dataSubSet$hor_acc,
                    hor_gyro = dataSubSet$hor_gyro,
                    ver_acc = dataSubSet$ver_acc,
                    ver_gyro = dataSubSet$ver_gyro
  )
  
  hor <- melt(hor ,  id = 'time', variable_name = 'series')
  
  p <- ggplot(hor, aes(time,value)) + geom_line(aes(colour = series), size=1) + xlab("Time") + ylab("Value")  + ggtitle(activity)
  return (p)
}
plotSideBySide <- function(data, activity){
  require(ggplot2)
  require(reshape)
  startTimestamp <- data$timestamp[data$activity == activity][1]+2000
  print(startTimestamp)
  dataSubSet <- data[data$activity == activity & data$timestamp > startTimestamp & data$timestamp < startTimestamp + 1000,]
  
  hor <- data.frame(time = dataSubSet$timestamp, 
                   hor_acc = dataSubSet$hor_acc,
                   hor_gyro = dataSubSet$hor_gyro
                   
  )
  
  hor <- melt(hor ,  id = 'time', variable_name = 'series')
  p1 <- ggplot(hor, aes(time,value)) + geom_line(aes(colour = series), size=1) + xlab("Time") + ylab("Value")  + ggtitle(activity)
  
  ver <- data.frame(time = dataSubSet$timestamp, 
                   ver_acc = dataSubSet$ver_acc,
                   ver_gyro = dataSubSet$ver_gyro
  )
  ver <- melt(ver ,  id = 'time', variable_name = 'series')
  p2 <- ggplot(ver, aes(time,value)) + geom_line(aes(colour = series), size=1)+ xlab("Time") + ylab("Value")
  sidebysideplot <- grid.arrange(p1, p2, nrow=2)

}

plotOne <- function(data, activity, col1, col2, start, size){
  require(ggplot2)
  require(reshape)
  require(grid)
  require(gridExtra)
  colnames(testRes)<- c("activity", "timestamp", "hor_acc", "ver_acc", "hor_gyro", "ver_gyro")
  
  startTimestamp <- data$timestamp[data$activity == activity][1] + start
  
  dataSubSet <- data[data$activity == activity & data$timestamp > startTimestamp & data$timestamp < startTimestamp + size,]
#   print(dataSubSet)
  df <- data.frame(time = dataSubSet$timestamp, 
                    one = dataSubSet[col1],
                    two = dataSubSet[col2]
  )
  
  df <- melt(df ,  id = 'time', variable_name = 'series')
  return(ggplot(df, aes(time,value)) + geom_line(aes(colour = series), size=1)  + xlab("Time") + ylab("Value")  + ggtitle(activity)) 
  
}
# plotSideBySide(testRes, "fast_walking")
