package pipelines.workshop.processor;

import java.util.ArrayList;
import java.util.Arrays;
import scala.collection.JavaConversions;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.testkit.TestKit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.scalatest.junit.JUnitSuite;
import pipelines.akkastream.testkit.javadsl.AkkaStreamletTestKit;
import pipelines.akkastream.testkit.javadsl.Completed;
import pipelines.akkastream.testkit.javadsl.ProbeOutletTap;
import pipelines.akkastream.testkit.javadsl.QueueInletTap;
import pipelines.workshop.schema.java.CardPayment;
import scala.concurrent.duration.Duration;

public class SanctionedCountriesFilterTest extends JUnitSuite {

    static ActorMaterializer mat;
    static ActorSystem system;

    @BeforeClass
    public static void setUp() throws Exception {
        system = ActorSystem.create();
        mat = ActorMaterializer.create(system);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        TestKit.shutdownActorSystem(system, Duration.create(10, "seconds"), false);
        system = null;
    }

    CardPayment p1 = new CardPayment(1538006400000L, "KR", "6fb55cb1-3aa6-472b-a99c-306081421ba6", "d6756993-2e55-4373-8feb-1b65e08828b9", "76f01f05-6df9-491d-8c82-13cc12d000af", 88.14);
    CardPayment p2 = new CardPayment(1538006400000L, "RU", "6fb55cb1-3aa6-472b-a99c-306081421ba6", "d6756993-2e55-4373-8feb-1b65e08828b9", "76f01f05-6df9-491d-8c82-13cc12d000af", 88.14);

    @Test
    public void testFlowProcessor() {
        SanctionedCountriesFilter streamlet = new SanctionedCountriesFilter();

        // 1. instantiate the testkit
        AkkaStreamletTestKit testkit = AkkaStreamletTestKit.create(system, mat);

        // 2. Setup inlet taps that tap the inlet ports of the streamlet
        QueueInletTap<CardPayment> in = testkit.makeInletAsTap(streamlet.inlet);

        // 3. Setup outlet probes for outlet ports
        ProbeOutletTap<CardPayment> manual = testkit.makeOutletAsTap(streamlet.manualOutlet);
        ProbeOutletTap<CardPayment> auto = testkit.makeOutletAsTap(streamlet.automaticOutlet);

        // 4. Push data into inlet ports
        in.queue().offer(p1);
        in.queue().offer(p2);

        // 5. Run the streamlet using the testkit and the setup inlet taps and outlet probes
        java.util.List outlets = Arrays.asList(new ProbeOutletTap[]{manual, auto});
        testkit.<CardPayment>run(streamlet, in, JavaConversions.asScalaBuffer(outlets).toList(), () -> {
            // 6. Assert
            return manual.probe().expectMsg(new akka.japi.Pair<String, CardPayment>("RU", p2));
        });

        // 6. Assert
        manual.probe().expectMsg(Completed.completed());
    }
}
