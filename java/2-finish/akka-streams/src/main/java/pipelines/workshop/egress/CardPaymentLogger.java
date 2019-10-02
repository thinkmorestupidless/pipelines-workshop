package pipelines.workshop.egress;

import akka.NotUsed;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import pipelines.akkastream.AkkaStreamlet;
import pipelines.akkastream.javadsl.RunnableGraphStreamletLogic;
import pipelines.streamlets.StreamletShape;
import pipelines.streamlets.avro.AvroInlet;
import pipelines.workshop.schema.java.CardPayment;

public class CardPaymentLogger extends AkkaStreamlet {

    AvroInlet<CardPayment> inlet = AvroInlet.<CardPayment>create("in", CardPayment.class);

    @Override
    public StreamletShape shape() {
        return StreamletShape.createWithInlets(inlet);
    }

    public RunnableGraphStreamletLogic createLogic() {
        return new RunnableGraphStreamletLogic(getStreamletContext()) {
            @Override
            public RunnableGraph<NotUsed> createRunnableGraph() {
                return getAtMostOnceSource(inlet).to(Sink.foreach(payment -> System.out.println("Hello, Card Payment! => " + payment)));
            }
        };
    }
}
