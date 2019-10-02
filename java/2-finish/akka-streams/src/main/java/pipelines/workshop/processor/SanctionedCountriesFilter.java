package pipelines.workshop.processor;

import akka.NotUsed;
import akka.stream.javadsl.FlowWithContext;
import pipelines.akkastream.AkkaStreamlet;
import pipelines.akkastream.PipelinesContext;
import pipelines.akkastream.javadsl.util.Either;
import pipelines.akkastream.util.javadsl.SplitterLogic;
import pipelines.streamlets.StreamletShape;
import pipelines.streamlets.avro.AvroInlet;
import pipelines.streamlets.avro.AvroOutlet;
import pipelines.workshop.schema.java.CardPayment;

public class SanctionedCountriesFilter extends AkkaStreamlet {

    AvroInlet<CardPayment> inlet = AvroInlet.<CardPayment>create("in", CardPayment.class);
    AvroOutlet<CardPayment> manualOutlet = AvroOutlet.<CardPayment>create("manual", CardPayment::countryCode, CardPayment.class);
    AvroOutlet<CardPayment> automaticOutlet = AvroOutlet.<CardPayment>create("auto", CardPayment::customerId, CardPayment.class);

    @Override
    public StreamletShape shape() {
        return StreamletShape.createWithInlets(inlet).withOutlets(manualOutlet, automaticOutlet);
    }

    @Override
    public SplitterLogic createLogic() {
        return new SplitterLogic<CardPayment, CardPayment, CardPayment>(inlet, manualOutlet, automaticOutlet, getStreamletContext()) {
            @Override
            public FlowWithContext<CardPayment, PipelinesContext, Either<CardPayment, CardPayment>, PipelinesContext, NotUsed> createFlow() {
                return createFlowWithPipelinesContext().map(this::checkForSanctions);
            }

            Either<CardPayment, CardPayment> checkForSanctions(CardPayment payment) {
                if (payment.countryCode().equals("RU")) {
                    return Either.left(payment);
                } else {
                    return Either.right(payment);
                }
            }
        };
    }

//    @Override
//    public StreamletLogic createLogic() {
//        return new RunnableGraphStreamletLogic(getStreamletContext()) {
//
//            public FlowWithContext<CardPayment, PipelinesContext, CardPayment, PipelinesContext, NotUsed> createFlow() {
//                return FlowWithContext.<CardPayment, PipelinesContext>create().filter(payment -> payment.getCountryCode().equals("RU"));
//            }
//
//            public RunnableGraph<NotUsed> createRunnableGraph() {
//                return getAtLeastOnceSource(inlet).via(createFlow()).to(getAtLeastOnceSink(outlet));
//            }
//        };
//    }
}
