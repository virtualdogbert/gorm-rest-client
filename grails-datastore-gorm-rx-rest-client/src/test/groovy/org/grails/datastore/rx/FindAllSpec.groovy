package org.grails.datastore.rx

import grails.http.MediaType
import org.grails.datastore.rx.domain.HalPerson
import org.grails.datastore.rx.domain.Person
import rx.Observable
import spock.lang.Ignore

import javax.print.attribute.standard.Media

/**
 * Created by graemerocher on 15/06/16.
 */
class FindAllSpec extends RxGormSpec {
    @Override
    List<Class> getDomainClasses() {
        [Person, HalPerson]
    }

    void 'Test parameterized query'() {
        given:
        def mock = client.expect {
            uri '/people/Test'
            accept(MediaType.HAL_JSON)
        }.respond {
            json( [
                    [
                            id: 1,
                            name: "Fred",
                            age: 10,
                            dateOfBirth: "2006-07-09T00:00+0000"],
                    [
                            id: 2,
                            name: "Joe",
                            age: 12,
                            dateOfBirth: "2004-07-09T00:00+0000"],
            ] )
        }

        when:""
        def results = Person.where {
            eq 'category', 'Test'
        }.findAll(uri:'/people/{category}').toList().toBlocking().first()

        then:
        results.size() == 2
    }

    void "Test the findAll method returns all objects when using HAL"() {
        given:"A canned response"
        def mock = client.expect {
            uri '/people'
            accept(MediaType.HAL_JSON)
        }
        .respond {
            contentType(MediaType.HAL_JSON)
            json {
                _embedded {
                    other {
                        foo "bar"
                    }
                    people( [
                            [
                                    id: 1,
                                    name: "Fred",
                                    age: 10,
                                    dateOfBirth: "2006-07-09T00:00+0000"],
                            [
                                    id: 2,
                                    name: "Joe",
                                    age: 12,
                                    dateOfBirth: "2004-07-09T00:00+0000"],
                    ])
                }
                totalCount 2
            }

        }

        when:"A get request is issued"
        Observable<HalPerson> observable = HalPerson.findAll()
        List<HalPerson> people = observable.toList().toBlocking().first()

        then:"The result is correct"
        mock.verify()
        people.size() == 2
        people[0].id == 1
        people[0].name == "Fred"
        people[0].age == 10
        people[1].id == 2
        people[1].name == "Joe"
        people[1].age == 12
    }

    void "Test the findAll method returns all objects"() {
        given:"A canned response"
        def mock = client.expect {
            uri '/people'
        }
        .respond {
            json( [
                [
                id: 1,
                name: "Fred",
                age: 10,
                dateOfBirth: "2006-07-09T00:00+0000"],
                [
                id: 2,
                name: "Joe",
                age: 12,
                dateOfBirth: "2004-07-09T00:00+0000"],
            ] )
        }

        when:"A get request is issued"
        Observable<Person> observable = Person.findAll()
        List<Person> people = observable.toList().toBlocking().first()

        then:"The result is correct"
        mock.verify()
        people.size() == 2
        people[0].id == 1
        people[0].name == "Fred"
        people[0].age == 10
        people[1].id == 2
        people[1].name == "Joe"
        people[1].age == 12
    }

    void "Test the findAll method paginates"() {
        given:"A canned response"
        def mock = client.expect {
            uri '/people?offset=10&max=20&sort=name&order=desc'
        }
        .respond {
            json( [
                    [
                            id: 1,
                            name: "Fred",
                            age: 10,
                            dateOfBirth: "2006-07-09T00:00+0000"],
                    [
                            id: 2,
                            name: "Joe",
                            age: 12,
                            dateOfBirth: "2004-07-09T00:00+0000"],
            ] )
        }

        when:"A get request is issued"
        List<Person> people = Person.list(max:20, offset:10, sort:"name", order:"desc").toBlocking().first()

        then:"The result is correct"
        mock.verify()
        people.size() == 2
        people[0].id == 1
        people[0].name == "Fred"
        people[0].age == 10
        people[1].id == 2
        people[1].name == "Joe"
        people[1].age == 12
    }

    void "Test the findAll method with id criterion returns a single object"() {
        given:"A canned response"
        def mock = client.expect {
            uri '/people/1'
        }
        .respond {
            json {
                id 1
                name "Fred"
                age 10
                dateOfBirth "2006-07-09T00:00+0000"
            }
        }

        when:"A get request is issued"
        Observable<Person> observable = Person.where {
            id == 1L
        }.find()
        Person p = observable.toBlocking().first()

        then:"The result is correct"
        mock.verify()
        p.name == "Fred"
        p.age == 10
        dateFormat.format(p.dateOfBirth) == "2006-07-09T00:00+0000"
    }

    void "Test the findAll method with id criterion and other criterion produces the right query"() {
        given:"A canned response"
        def mock = client.expect {
            uri '/people/1?age=10'
        }
        .respond {
            json {
                id 1
                name "Fred"
                age 10
                dateOfBirth "2006-07-09T00:00+0000"
            }
        }

        when:"A get request is issued"
        Observable<Person> observable = Person.where {
            id == 1L && age == 10
        }.find()
        Person p = observable.toBlocking().first()

        then:"The result is correct"
        mock.verify()
        p.name == "Fred"
        p.age == 10
        dateFormat.format(p.dateOfBirth) == "2006-07-09T00:00+0000"
    }
}
