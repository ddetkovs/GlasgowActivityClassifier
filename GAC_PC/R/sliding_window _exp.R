ggplot(slidingwindow1, aes(x = time, y = precision, colour="1")) + 
  geom_line(size=1) +
  geom_line(data = slidingwindow2, aes(y=precision,colour="2"), size=1) +
  scale_colour_manual(name="Moving Classification\nMethod", values=c("red", "blue"), labels=c("NaiveBayes", "SVM"))+
  xlab("Segment Size (ms)") + ylab("Average Precision") +
  theme(legend.position="bottom")

