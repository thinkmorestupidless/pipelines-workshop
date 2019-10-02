package pipelines.workshop.egress;

import akka.NotUsed;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import pipelines.akkastream.AkkaStreamlet;
import pipelines.akkastream.javadsl.RunnableGraphStreamletLogic;
import pipelines.streamlets.StreamletShape;
import pipelines.streamlets.avro.AvroInlet;
import pipelines.workshop.schema.scala.CustomerSpendSummary;

public class CustomerSpendLogger extends AkkaStreamlet {

    AvroInlet<CustomerSpendSummary> inlet = AvroInlet.<CustomerSpendSummary>create("in", CustomerSpendSummary.class);

    @Override
    public StreamletShape shape() {
        return StreamletShape.createWithInlets(inlet);
    }

    public RunnableGraphStreamletLogic createLogic() {
        return new RunnableGraphStreamletLogic(getStreamletContext()) {
            @Override
            public RunnableGraph<NotUsed> createRunnableGraph() {
                return getAtMostOnceSource(inlet).to(Sink.foreach(payment -> System.out.println("Customer Spend Summary => " + payment)));
            }
        };
    }
}
