package org.grails.datastore.rx

import groovy.transform.CompileStatic
import org.grails.datastore.bson.json.JsonWriter
import org.grails.datastore.rx.rest.test.TestRxRestDatastoreClient
import spock.lang.AutoCleanup
import spock.lang.Specification

import java.text.DateFormat
import java.text.SimpleDateFormat

@CompileStatic
abstract class RxGormSpec extends Specification {

    @AutoCleanup TestRxRestDatastoreClient client
    DateFormat dateFormat

    void setup() {
        dateFormat = new SimpleDateFormat(JsonWriter.ISO_8601)
        TimeZone UTC = TimeZone.getTimeZone("UTC");
        dateFormat.setTimeZone(UTC)

        def classes = getDomainClasses()
        client = createRestDatastoreClient(classes)
    }

    protected TestRxRestDatastoreClient createRestDatastoreClient(List<Class> classes) {
        new TestRxRestDatastoreClient(classes as Class[])
    }

    abstract List<Class> getDomainClasses()
}
