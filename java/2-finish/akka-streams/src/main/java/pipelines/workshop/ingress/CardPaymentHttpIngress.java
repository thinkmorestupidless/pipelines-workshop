package pipelines.workshop.ingress;

import akka.http.javadsl.common.EntityStreamingSupport;
import akka.http.javadsl.marshallers.jackson.Jackson;
import pipelines.akkastream.AkkaServerStreamlet;
import pipelines.akkastream.StreamletLogic;
import pipelines.akkastream.util.javadsl.HttpServerLogic;
import pipelines.streamlets.StreamletShape;
import pipelines.streamlets.avro.AvroOutlet;
import pipelines.workshop.schema.java.CardPayment;

public class CardPaymentHttpIngress extends AkkaServerStreamlet {

    AvroOutlet<CardPayment> outlet = AvroOutlet.<CardPayment>create("out", CardPayment.class);

    @Override
    public StreamletShape shape() {
        return StreamletShape.createWithOutlets(outlet);
    }

    @Override
    public StreamletLogic createLogic() {
        EntityStreamingSupport ess = EntityStreamingSupport.json();
        return HttpServerLogic.createDefaultStreaming(this, outlet, Jackson.byteStringUnmarshaller(CardPayment.class), ess, getStreamletContext());
    }
}
