package com.klipwallet.membership.entity


import org.apache.commons.lang3.reflect.FieldUtils
import spock.lang.Specification

class NoticeTest extends Specification {
    Notice notice
    MemberId creatorId

    void setup() {
        creatorId = new MemberId(1)
        notice = new Notice("title", "body", creatorId)
        FieldUtils.writeField(notice, "id", 10, true)
    }

    def "PrimaryOff"() {
        given:
        notice.primaryOn()

        when:
        notice.primaryOff()

        then:
        !notice.primary
    }
}
