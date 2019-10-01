package pipelines.workshop.processor;

import akka.actor.ActorSystem;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.unmarshalling.Unmarshaller;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.util.ByteString;
import junit.framework.TestCase;
import pipelines.workshop.schema.CardPayment;

import java.util.concurrent.CompletableFuture;

public class CardPaymentTest extends TestCase {

    public void testUnmarshalling() throws Exception {
        ActorSystem system = ActorSystem.create("Test-System");
        Materializer mat = ActorMaterializer.create(system);

        String jsonStr = "{\"timestamp\":1538006400000,\"countryCode\":\"KR\",\"customerId\":\"6fb55cb1-3aa6-472b-a99c-306081421ba6\",\"deviceId\":\"d6756993-2e55-4373-8feb-1b65e08828b9\",\"merchantId\":\"76f01f05-6df9-491d-8c82-13cc12d000af\",\"amount\":88.14}";
        CardPayment expected = new CardPayment(1538006400000L, "KR", "6fb55cb1-3aa6-472b-a99c-306081421ba6", "d6756993-2e55-4373-8feb-1b65e08828b9", "76f01f05-6df9-491d-8c82-13cc12d000af", 88.14);

        Unmarshaller<ByteString, CardPayment> unmarshaller = Jackson.<CardPayment>byteStringUnmarshaller(CardPayment.class);
        ByteString byteString = ByteString.fromString(jsonStr);

        CompletableFuture<CardPayment> f = (CompletableFuture<CardPayment>) unmarshaller.unmarshal(byteString, mat);

        assertEquals(expected, f.get());
    }
}
