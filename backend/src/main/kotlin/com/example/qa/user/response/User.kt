package com.example.qa.user.response

import lombok.Getter
import lombok.ToString
import lombok.NoArgsConstructor
import lombok.Setter
import org.hibernate.Hibernate
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
class User(
    @field:Id private val username: String,
    private var password: String,
    private var email: String = "",
    private var phone: String = "",
    private var birthday: String = "",
    private var gender: String = "",
    private var permission: String = "",
    private var money: String = "100",
    private var description: String = ""
) {
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false
        val user = o as User
        return username == user.username
    }

    override fun hashCode(): Int {
        return Objects.hash(
            username,
            email,
            phone,
            password,
            birthday,
            gender,
            permission,
            money,
            description
        )
    }
}