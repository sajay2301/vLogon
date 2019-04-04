package com.vlogonappv1.db


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import com.vlogonappv1.dataclass.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*


class DBHelper(private val mContext: Context
) : SQLiteOpenHelper(mContext, DATABASE_NAME, null, databaseVersion) {


    companion object {

        //Database Version
        val databaseVersion = 8

        //Database Name
        val DATABASE_NAME = "vlogonappbatabase8"

        //Data Type
        private val TEXT = " TEXT "
        private val INTEGER = " INTEGER "

        //Tables Name
        private val TABLE_UserRegistration = "userregistration"
        private val TABLE_EDUCATION = "education"
        private val TABLE_ACHIEVEMENT = "achievement"
        private val TABLE_EXPERIENCE = "experience"
        private val TABLE_SKILL = "skill"

        //UserRegistration Table - column name
        private val USER_ID = "id"
        private val FirstName = "firstname"
        private val LastName = "lastname"
        private val MobileNumber = "mobilenumber"

        private val PersonalEmail = "personalemail"
        private val OfficeEmail = "officeemail"
        private val Password = "password"

        private val UserName = "username"
        private val BirthDate = "birthdate"

        private val Source = "source"
        private val ProfilePic = "profilepic"
        private val Gender = "gender"

        private val Address = "address"
        private val City = "city"
        private val Country = "country"
        private val Countrycode = "countrycode"

        private val AdditionalNumber = "additionalnumber"


        //Education Table - column name

        private val Education_ID = "eid"
        private val Edu_Email = "edu_email"
        private val Edu_source = "edusource"
        private val Edu_degree = "edudegree"

        private val Edu_fieldtostudy = "edufieldtostudy"
        private val Edu_college = "educollege"
        private val Edu_Yearofcomletion = "eduyearofcomletion"


        //ACHIEVEMENT Table - column name


        private val ACHIEVEMENT_ID = "aid"
        private val ACHIEVEMENT_Email = "achieveemail"
        private val ACHIEVEMENT_Source = "achievesource"
        private val ACHIEVEMENT_Company = "achievecompany"
        private val ACHIEVEMENT_Title = "achievetitle"

        private val ACHIEVEMENT_Link = "achievelink"
        private val ACHIEVEMENT_Discription = "achievediscription"


        //SKILL Table - column name


        private val SKILL_ID = "sid"
        private val SKILL_Email = "skillemail"
        private val SKILL_Source = "skillsource"
        private val SKILL_skills = "skillenter"


        //EXPERIENCE Table - column name

        private val EXPERIENCE_ID = "exid"
        private val EXPERIENCE_Email = "experienceemail"
        private val EXPERIENCE_Source = "experiencesource"
        private val EXPERIENCE_Company = "experiencecompany"
        private val EXPERIENCE_Title= "experiencetitle"
        private val EXPERIENCE_Location = "experiencelocation"
        private val EXPERIENCE_Startdate = "experiencestartdate"
        private val EXPERIENCE_Enddate = "experienceenddate"
        private val EXPERIENCE_Description = "experiencedescription"
        private val EXPERIENCE_Currentlyworkingornot = "currentlyworkingornot"


        private val TABLE_CONTACT = "dbContacts"
        private val CONTACT_ID = "cid"
        private val CONTACT_Firstname = "contact_firstname"
        private val CONTACT_Lastname = "contact_lastname"
        private val CONTACT_NUMBER = "contact_number"
        private val CONTACT_EmailID = "contact_emailid"
        private val CONTACT_UserName = "contact_username"
        private val CONTACT_Birthdate = "contact_birthdate"
        private val CONTACT_ProfilePic = "contact_profilepic"
        private val CONTACT_Gender = "contact_gender"
        private val CONTACT_Address = "contact_address"
        private val CONTACT_Countrycode = "contact_countrycode"
        private val CONTACT_AdditionalNumber = "contact_additionalnumber"
        private val CONTACT_Tage = "contact_tage"
        private val CONTACT_Status = "contact_status"


        private val TABLE_BLOCKCONTACT = "BlockContacts"
        private val BLOCK_ID = "blockcid"
        private val BLOCK_Firstname = "block_firstname"
        private val BLOCK_Lastname = "block_lastname"
        private val BLOCK_NUMBER = "block_number"
        private val BLOCK_EmailID = "block_emailid"
        private val BLOCK_UserName = "block_username"
        private val BLOCK_Birthdate = "block_birthdate"
        private val BLOCK_ProfilePic = "block_profilepic"
        private val BLOCK_Gender = "block_gender"
        private val BLOCK_Address = "block_address"
        private val BLOCK_Countrycode = "block_countrycode"
        private val BLOCK_AdditionalNumber = "block_additionalnumber"
        private val BLOCK_Tage = "block_tage"
        private val BLOCK_Status = "block_status"

        private val TABLE_RegisterCONTACT = "RegisterContacts"
        private val Register_ID = "registercid"
        private val Register_Firstname = "register_firstname"
        private val Register_Lastname = "register_lastname"
        private val Register_NUMBER = "register_number"
        private val Register_EmailID = "register_emailid"
        private val Register_UserName = "register_username"
        private val Register_Birthdate = "register_birthdate"
        private val Register_ProfilePic = "register_profilepic"
        private val Register_Gender = "register_gender"
        private val Register_Address = "register_address"
        private val Register_Countrycode = "register_countrycode"
        private val Register_AdditionalNumber = "register_additionalnumber"
        private val Register_Tage = "register_tage"



        private val TABLE_DUBLICATECONTACT = "duplicateContacts"
        private val DUPLICATE_ID = "duplicateid"
        private val DUPLICATE_Firstname = "duplicate_firstname"
        private val DUPLICATE_Lastname = "duplicate_lastname"
        private val DUPLICATE_NUMBER = "duplicate_number"
        private val DUPLICATE_EmailID = "duplicate_emailid"
        private val DUPLICATE_UserName = "duplicate_username"
        private val DUPLICATE_Birthdate = "duplicate_birthdate"
        private val DUPLICATE_ProfilePic = "duplicate_profilepic"
        private val DUPLICATE_Gender = "duplicate_gender"
        private val DUPLICATE_Address = "duplicate_address"
        private val DUPLICATE_Countrycode = "duplicate_countrycode"
        private val DUPLICATE_AdditionalNumber = "duplicate_additionalnumber"
        private val DUPLICATE_Tage = "duplicate_tage"
        private val DUPLICATE_Status = "duplicate_status"

        //Students table
        private val CREATE_TABLE_STUDENTS = "CREATE TABLE " + TABLE_UserRegistration + "(" +
                USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                UserName + " TEXT," +
                FirstName + " TEXT," +
                LastName + " TEXT," +
                MobileNumber + " TEXT," +
                AdditionalNumber + " TEXT," +
                BirthDate + " TEXT," +
                PersonalEmail + " TEXT," +
                OfficeEmail + " TEXT," +
                Password + " TEXT," +
                Source + " TEXT," +
                ProfilePic + " TEXT," +
                Gender + " TEXT," +
                Address + " TEXT," +
                City + " TEXT," +
                Country + " TEXT," +
                Countrycode + " TEXT" + ")"


        private val CREATE_TABLE_EDUCATION = "CREATE TABLE " + TABLE_EDUCATION + "(" +
                Education_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Edu_Email + " TEXT," +
                Edu_source + " TEXT," +
                Edu_degree + " TEXT," +
                Edu_fieldtostudy + " TEXT," +
                Edu_college + " TEXT," +
                Edu_Yearofcomletion + " TEXT" + ")"

        private val CREATE_TABLE_ACHIEVMENT = "CREATE TABLE " + TABLE_ACHIEVEMENT + "(" +
                ACHIEVEMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ACHIEVEMENT_Email + " TEXT," +
                ACHIEVEMENT_Source + " TEXT," +
                ACHIEVEMENT_Company + " TEXT," +
                ACHIEVEMENT_Title + " TEXT," +
                ACHIEVEMENT_Link + " TEXT," +
                ACHIEVEMENT_Discription + " TEXT" + ")"


        private val CREATE_TABLE_SKILL = "CREATE TABLE " + TABLE_SKILL + "(" +
                SKILL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SKILL_Email + " TEXT," +
                SKILL_Source + " TEXT," +
                SKILL_skills + " TEXT" + ")"



        private val CREATE_TABLE_EXPERIENCE = "CREATE TABLE " + TABLE_EXPERIENCE + "(" +
                EXPERIENCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                EXPERIENCE_Email + " TEXT," +
                EXPERIENCE_Source + " TEXT," +
                EXPERIENCE_Company + " TEXT," +
                EXPERIENCE_Title + " TEXT," +
                EXPERIENCE_Location + " TEXT," +
                EXPERIENCE_Startdate + " TEXT," +
                EXPERIENCE_Enddate + " TEXT," +
                EXPERIENCE_Description + " TEXT," +
                EXPERIENCE_Currentlyworkingornot + " TEXT" + ")"
        //Table Delete Statement



        private val CREATE_TABLE_DUBLICATECONTACT="CREATE TABLE " + TABLE_DUBLICATECONTACT + "(" +
                DUPLICATE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DUPLICATE_UserName + " TEXT, " +
                DUPLICATE_NUMBER + " TEXT, " +
                DUPLICATE_EmailID + " TEXT, " +
                DUPLICATE_Firstname + " TEXT, " +
                DUPLICATE_Lastname + " TEXT, " +
                DUPLICATE_Birthdate + " TEXT, " +
                DUPLICATE_ProfilePic + " TEXT, " +
                DUPLICATE_Gender + " TEXT, " +
                DUPLICATE_Address + " TEXT, " +
                DUPLICATE_Countrycode + " TEXT, " +
                DUPLICATE_AdditionalNumber + " TEXT, " +
                DUPLICATE_Tage + " TEXT, " +
                DUPLICATE_Status + " TEXT " +
        ")"


        private val CREATE_TABLE_CONTACT="CREATE TABLE " + TABLE_CONTACT + "(" +
                CONTACT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CONTACT_UserName + " TEXT, " +
                CONTACT_NUMBER + " TEXT, " +
                CONTACT_EmailID + " TEXT, " +
                CONTACT_Firstname + " TEXT, " +
                CONTACT_Lastname + " TEXT, " +
                CONTACT_Birthdate + " TEXT, " +
                CONTACT_ProfilePic + " TEXT, " +
                CONTACT_Gender + " TEXT, " +
                CONTACT_Address + " TEXT, " +
                CONTACT_Countrycode + " TEXT, " +
                CONTACT_AdditionalNumber + " TEXT, " +
                CONTACT_Tage + " TEXT, " +
                CONTACT_Status + " TEXT " +
                ")"

        private val CREATE_TABLE_BLOCKCONTACT="CREATE TABLE " + TABLE_BLOCKCONTACT + "(" +
                BLOCK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                BLOCK_UserName + " TEXT, " +
                BLOCK_NUMBER + " TEXT, " +
                BLOCK_EmailID + " TEXT, " +
                BLOCK_Firstname + " TEXT, " +
                BLOCK_Lastname + " TEXT, " +
                BLOCK_Birthdate + " TEXT, " +
                BLOCK_ProfilePic + " TEXT, " +
                BLOCK_Gender + " TEXT, " +
                BLOCK_Address + " TEXT, " +
                BLOCK_Countrycode + " TEXT, " +
                BLOCK_AdditionalNumber + " TEXT, " +
                BLOCK_Tage + " TEXT, " +
                BLOCK_Status + " TEXT " +
                ")"


        private val CREATE_TABLE_REGISTERCONTACT="CREATE TABLE " + TABLE_RegisterCONTACT + "(" +
                Register_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Register_UserName + " TEXT, " +
                Register_NUMBER + " TEXT, " +
                Register_EmailID + " TEXT, " +
                Register_Firstname + " TEXT, " +
                Register_Lastname + " TEXT, " +
                Register_Birthdate + " TEXT, " +
                Register_ProfilePic + " TEXT, " +
                Register_Gender + " TEXT, " +
                Register_Address + " TEXT, " +
                Register_Countrycode + " TEXT, " +
                Register_AdditionalNumber + " TEXT, " +
                Register_Tage + " TEXT " +
                ")"



        //Students Table
        private val DELETE_STUDENTS = "DROP TABLE IF EXISTS $TABLE_UserRegistration"
        private val DELETE_EDUCATION = "DROP TABLE IF EXISTS $TABLE_EDUCATION"
        private val DELETE_ACHIEVEMENT = "DROP TABLE IF EXISTS $TABLE_ACHIEVEMENT"
        private val DELETE_SKILL = "DROP TABLE IF EXISTS $TABLE_SKILL"
        private val DELETE_EXPERIENCE = "DROP TABLE IF EXISTS $TABLE_EXPERIENCE"
        private val DELETE_CONTACT = "DROP TABLE IF EXISTS $TABLE_CONTACT"
        private val DELETE_BLOCKCONTACT = "DROP TABLE IF EXISTS $TABLE_BLOCKCONTACT"
        private val DELETE_RegisterCONTACT = "DROP TABLE IF EXISTS $TABLE_RegisterCONTACT"
        private val DELETE_DUBLICATECONTACT = "DROP TABLE IF EXISTS $TABLE_DUBLICATECONTACT"
        //Exams Table

    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_STUDENTS)
        db.execSQL(CREATE_TABLE_EDUCATION)
        db.execSQL(CREATE_TABLE_ACHIEVMENT)
        db.execSQL(CREATE_TABLE_SKILL)
        db.execSQL(CREATE_TABLE_EXPERIENCE)
        db.execSQL(CREATE_TABLE_CONTACT)
        db.execSQL(CREATE_TABLE_BLOCKCONTACT)
        db.execSQL(CREATE_TABLE_REGISTERCONTACT)
        db.execSQL(CREATE_TABLE_DUBLICATECONTACT)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        //reinitialize db
        db.execSQL(DELETE_STUDENTS)
        db.execSQL(DELETE_EDUCATION)
        db.execSQL(DELETE_ACHIEVEMENT)
        db.execSQL(DELETE_SKILL)
        db.execSQL(DELETE_EXPERIENCE)
        db.execSQL(DELETE_CONTACT)
        db.execSQL(DELETE_BLOCKCONTACT)
        db.execSQL(DELETE_RegisterCONTACT)
        db.execSQL(DELETE_DUBLICATECONTACT)
        Log.e("dbhelper", "Database Version: OLD: "+ oldVersion + " = NEW: "+newVersion);
        onCreate(db)
    }

    fun deleteAll(db: SQLiteDatabase) {

        db.execSQL(DELETE_STUDENTS)
        db.execSQL(DELETE_EDUCATION)

    }

    //create a new student
    fun addUserRegistration(stud: UserRegistrationClass): Long {

        // Gets the data repository in write mode
        val db = this.writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(UserName, stud.username)
        values.put(FirstName, stud.firstname)
        values.put(LastName, stud.lastname)
        values.put(MobileNumber, stud.mobilenumber)
        values.put(AdditionalNumber, stud.additionalnumber)
        values.put(BirthDate, stud.birthdate)
        values.put(PersonalEmail, stud.personalemail)
        values.put(OfficeEmail, stud.officeemail)
        values.put(Password, stud.password)
        values.put(Source, stud.source)
        values.put(ProfilePic, stud.profilepic)
        values.put(Gender, stud.gender)
        values.put(Address, stud.address)
        values.put(City, stud.city)
        values.put(Country, stud.country)
        values.put(Countrycode, stud.countrycode)

        // Create a new map of values, where column names are the keys
        return db.insert(TABLE_UserRegistration, null, values)
    }

    fun updateUserRegistration(stud: UserRegistrationClass) {
        val db = this.writableDatabase

        val values = ContentValues()

        values.put(FirstName, stud.firstname)
        values.put(LastName, stud.lastname)
        values.put(MobileNumber, stud.mobilenumber)
        values.put(PersonalEmail, stud.personalemail)
        values.put(OfficeEmail, stud.officeemail)
        values.put(Password, stud.password)
        values.put(Source, stud.source)
        values.put(ProfilePic, stud.profilepic)
        values.put(Gender, stud.gender)
        values.put(Address, stud.address)
        values.put(City, stud.city)
        values.put(Country, stud.country)
        values.put(Countrycode, stud.countrycode)
        values.put(BirthDate, stud.birthdate)
        values.put(AdditionalNumber, stud.additionalnumber)

        // updating row
        db.update(TABLE_UserRegistration, values, ""+ PersonalEmail+"= '"+ stud.personalemail +"' AND "+ Source+"='"+stud.source+"'",
            null)
        db.close()
    }

    fun addEducationDetails(edu: EducationDataClass): Long {

        // Gets the data repository in write mode
        val db = this.writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()

        values.put(Edu_Email, edu.personalemail)
        values.put(Edu_source, edu.source)
        values.put(Edu_degree, edu.degree)
        values.put(Edu_fieldtostudy, edu.fieldtostudy)
        values.put(Edu_college, edu.college)
        values.put(Edu_Yearofcomletion, edu.Yearofcomletion)

        // Create a new map of values, where column names are the keys
        return db.insert(TABLE_EDUCATION, null, values)
    }


    fun updateEducationDetails(edu: EducationDataClass) {

        // Gets the data repository in write mode
        val db = this.writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()

        values.put(Edu_Email, edu.personalemail)
        values.put(Edu_source, edu.source)
        values.put(Edu_degree, edu.degree)
        values.put(Edu_fieldtostudy, edu.fieldtostudy)
        values.put(Edu_college, edu.college)
        values.put(Edu_Yearofcomletion, edu.Yearofcomletion)

        // Create a new map of values, where column names are the keys
        db.update(
            TABLE_EDUCATION, values, ""+Edu_Email+"= '"+ edu.personalemail+"' AND "+ Education_ID+"='"+edu.eid+"'  AND "+Edu_source+"='"+edu.source+"'",
            null)

        db.close()
    }

    fun addAchievementDetails(achieve: AchievementsDataClass): Long {

        // Gets the data repository in write mode
        val db = this.writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(ACHIEVEMENT_Email, achieve.personalemail)
        values.put(ACHIEVEMENT_Source, achieve.source)
        values.put(ACHIEVEMENT_Company, achieve.company)
        values.put(ACHIEVEMENT_Title, achieve.title)
        values.put(ACHIEVEMENT_Link, achieve.link)
        values.put(ACHIEVEMENT_Discription, achieve.discription)

        // Create a new map of values, where column names are the keys
        return db.insert(TABLE_ACHIEVEMENT, null, values)
    }

    fun updateAchievementDetails(achieve: AchievementsDataClass) {

        // Gets the data repository in write mode
        val db = this.writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(ACHIEVEMENT_Email, achieve.personalemail)
        values.put(ACHIEVEMENT_Source, achieve.source)
        values.put(ACHIEVEMENT_Company, achieve.company)
        values.put(ACHIEVEMENT_Title, achieve.title)
        values.put(ACHIEVEMENT_Link, achieve.link)
        values.put(ACHIEVEMENT_Discription, achieve.discription)

        // Create a new map of values, where column names are the keys
        db.update(
            TABLE_ACHIEVEMENT, values, ""+ACHIEVEMENT_Email+"= '"+ achieve.personalemail+"' AND "+ ACHIEVEMENT_ID+"='"+achieve.aid+"'  AND "+ ACHIEVEMENT_Source+"='"+achieve.source+"'",
            null)

        db.close()
    }


    fun addSkillDetails(skill: SkillsDataClass): Long {

        // Gets the data repository in write mode
        val db = this.writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(SKILL_Email, skill.personalemail)
        values.put(SKILL_Source, skill.source)
        values.put(SKILL_skills, skill.skills)


        // Create a new map of values, where column names are the keys
        return db.insert(TABLE_SKILL, null, values)
    }

    fun updateSkillDetails(skill: SkillsDataClass) {

        // Gets the data repository in write mode
        val db = this.writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(SKILL_Email, skill.personalemail)
        values.put(SKILL_Source, skill.source)
        values.put(SKILL_skills, skill.skills)


        // Create a new map of values, where column names are the keys
        db.update(
            TABLE_SKILL, values, ""+SKILL_Email+"= '"+ skill.personalemail+"' AND "+ SKILL_ID+"='"+skill.sid+"'  AND "+ SKILL_Source+"='"+skill.source+"'",
            null)

        db.close()
    }

    fun addExperienceDetails(experience: ExperienceDataClass): Long {

        // Gets the data repository in write mode
        val db = this.writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(EXPERIENCE_Email, experience.personalemail)
        values.put(EXPERIENCE_Source, experience.source)
        values.put(EXPERIENCE_Company, experience.company)
        values.put(EXPERIENCE_Title, experience.title)
        values.put(EXPERIENCE_Location, experience.location)
        values.put(EXPERIENCE_Startdate, experience.startdate)
        values.put(EXPERIENCE_Enddate, experience.enddate)
        values.put(EXPERIENCE_Description, experience.description)
        values.put(EXPERIENCE_Currentlyworkingornot, experience.currentlyworkingornot)
        // Create a new map of values, where column names are the keys
        return db.insert(TABLE_EXPERIENCE, null, values)
    }


    fun updateExperienceDetails(experience: ExperienceDataClass) {

        // Gets the data repository in write mode
        val db = this.writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(EXPERIENCE_Email, experience.personalemail)
        values.put(EXPERIENCE_Source, experience.source)
        values.put(EXPERIENCE_Company, experience.company)
        values.put(EXPERIENCE_Title, experience.title)
        values.put(EXPERIENCE_Location, experience.location)
        values.put(EXPERIENCE_Startdate, experience.startdate)
        values.put(EXPERIENCE_Enddate, experience.enddate)
        values.put(EXPERIENCE_Description, experience.description)
        values.put(EXPERIENCE_Currentlyworkingornot, experience.currentlyworkingornot)
        // Create a new map of values, where column names are the keys
        db.update(
            TABLE_EXPERIENCE, values, ""+EXPERIENCE_Email+"= '"+ experience.personalemail+"' AND "+ EXPERIENCE_ID+"='"+experience.eid+"'  AND "+EXPERIENCE_Source+"='"+experience.source+"'",
            null)

        db.close()
    }
    //delete a student
    fun deleteStud(id: Int) {

        val db = writableDatabase
        db.delete(TABLE_UserRegistration, "$USER_ID = ? ", arrayOf(id.toString()))
    }


    //close database
    fun closeDB() {

        val db = this.readableDatabase

        if (db != null && db.isOpen)
            db.close()
    }


    fun backup(outFileName: String) {

        //database path
        val inFileName = mContext.getDatabasePath(DATABASE_NAME).toString()

        try {

            val dbFile = File(inFileName)
            val fis = FileInputStream(dbFile)

            // Open the empty db as the output stream
            val output = FileOutputStream(outFileName)

            // Transfer bytes from the input file to the output file
            val buffer = ByteArray(1024)
            var length: Int = 0

            fis.use { input ->
                output.use { fileOut ->

                    while (true) {
                        val length = input.read(buffer)
                        if (length <= 0)
                            break
                        fileOut.write(buffer, 0, length)
                    }

                }
            }
            // Close the streams
            output.flush()
            output.close()
            fis.close()

            Toast.makeText(mContext, "Backup Completed", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(mContext, "Unable to backup database. Retry", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }

    }

    fun importDB(inFileName: String) {

        val outFileName = mContext.getDatabasePath(DATABASE_NAME).toString()

        try {

            val dbFile = File(inFileName)
            val fis = FileInputStream(dbFile)
            // Open the empty db as the output stream
            val output = FileOutputStream(outFileName)

            // Transfer bytes from the input file to the output file
            val buffer = ByteArray(1024)
            var length: Int = 0

            fis.use { input ->
                output.use { fileOut ->

                    while (true) {
                        val length = input.read(buffer)
                        if (length <= 0)
                            break
                        fileOut.write(buffer, 0, length)
                    }

                }
            }

            // Close the streams
            output.flush()
            output.close()
            fis.close()

            Toast.makeText(mContext, "Import Completed", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(mContext, "Unable to import database. Retry", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }

    }

    //get all students
    //select all query


    fun allStudent(emailid: String, source: String): List<UserRegistrationClass> {
        val students = ArrayList<UserRegistrationClass>()
        val query = "SELECT * FROM $TABLE_UserRegistration WHERE $PersonalEmail = '$emailid' AND $Source = '$source'"

        val db = this.readableDatabase
        val c = db.rawQuery(query, null)
        if (c != null) {
            while (c.moveToNext()) {

                val student = UserRegistrationClass()
                student.registerid = c.getInt(c.getColumnIndex(USER_ID))
                student.firstname = c.getString(c.getColumnIndex(FirstName))
                student.lastname = c.getString(c.getColumnIndex(LastName))
                student.personalemail = c.getString(c.getColumnIndex(PersonalEmail))
                student.profilepic = c.getString(c.getColumnIndex(ProfilePic))
                student.officeemail = c.getString(c.getColumnIndex(OfficeEmail))
                student.mobilenumber = c.getString(c.getColumnIndex(MobileNumber))
                student.countrycode = c.getString(c.getColumnIndex(Countrycode))

                student.gender = c.getString(c.getColumnIndex(Gender))
                student.city = c.getString(c.getColumnIndex(City))
                student.country = c.getString(c.getColumnIndex(Country))
                student.address = c.getString(c.getColumnIndex(Address))

                students.add(student)
            }
        }
        c!!.close()
        return students
    }

    fun LoginUser(emailid: String, password: String): List<UserRegistrationClass> {
        val students = ArrayList<UserRegistrationClass>()
        val query = "SELECT * FROM $TABLE_UserRegistration WHERE $PersonalEmail = '$emailid' AND $Password = '$password'"

        val db = this.readableDatabase
        val c = db.rawQuery(query, null)
        if (c != null) {
            while (c.moveToNext()) {

                val student = UserRegistrationClass()
                student.registerid = c.getInt(c.getColumnIndex(USER_ID))
                student.firstname = c.getString(c.getColumnIndex(FirstName))
                student.lastname = c.getString(c.getColumnIndex(LastName))
                student.personalemail = c.getString(c.getColumnIndex(PersonalEmail))
                student.source = c.getString(c.getColumnIndex(Source))
                student.profilepic = c.getString(c.getColumnIndex(ProfilePic))
                student.officeemail = c.getString(c.getColumnIndex(OfficeEmail))
                student.mobilenumber = c.getString(c.getColumnIndex(MobileNumber))
                student.countrycode = c.getString(c.getColumnIndex(Countrycode))

                student.gender = c.getString(c.getColumnIndex(Gender))
                student.city = c.getString(c.getColumnIndex(City))
                student.country = c.getString(c.getColumnIndex(Country))
                student.address = c.getString(c.getColumnIndex(Address))

                students.add(student)
            }
        }
        c!!.close()
        return students
    }

    fun allEducationDetails(emailid: String, source: String): List<EducationDataClass> {
        val educationdetails = ArrayList<EducationDataClass>()
        val query = "SELECT * FROM $TABLE_EDUCATION WHERE $Edu_Email = '$emailid' AND $Edu_source = '$source' ORDER BY $Edu_Yearofcomletion"

        val db = this.readableDatabase
        val c = db.rawQuery(query, null)
        if (c != null) {
            while (c.moveToNext()) {

                val education = EducationDataClass()
                education.eid = c.getInt(c.getColumnIndex(Education_ID))
                education.personalemail = c.getString(c.getColumnIndex(Edu_Email))
                education.source = c.getString(c.getColumnIndex(Edu_source))
                education.degree = c.getString(c.getColumnIndex(Edu_degree))
                education.fieldtostudy = c.getString(c.getColumnIndex(Edu_fieldtostudy))
                education.college = c.getString(c.getColumnIndex(Edu_college))
                education.Yearofcomletion = c.getString(c.getColumnIndex(Edu_Yearofcomletion))

                educationdetails.add(education)
            }
        }
        c!!.close()
        return educationdetails
    }


    fun allAchievmentDetails(emailid: String, source: String): List<AchievementsDataClass> {
        val achievmentdetails = ArrayList<AchievementsDataClass>()
        val query = "SELECT * FROM $TABLE_ACHIEVEMENT WHERE $ACHIEVEMENT_Email = '$emailid' AND $ACHIEVEMENT_Source = '$source'"

        val db = this.readableDatabase
        val c = db.rawQuery(query, null)
        if (c != null) {
            while (c.moveToNext()) {

                val achievements = AchievementsDataClass()
                achievements.aid = c.getInt(c.getColumnIndex(ACHIEVEMENT_ID))
                achievements.personalemail = c.getString(c.getColumnIndex(ACHIEVEMENT_Email))
                achievements.source = c.getString(c.getColumnIndex(ACHIEVEMENT_Source))
                achievements.company = c.getString(c.getColumnIndex(ACHIEVEMENT_Company))
                achievements.title = c.getString(c.getColumnIndex(ACHIEVEMENT_Title))
                achievements.link = c.getString(c.getColumnIndex(ACHIEVEMENT_Link))
                achievements.discription = c.getString(c.getColumnIndex(ACHIEVEMENT_Discription))

                achievmentdetails.add(achievements)
            }
        }
        c!!.close()
        return achievmentdetails
    }

    fun allSkillDetails(emailid: String, source: String): List<SkillsDataClass> {
        val skilldetails = ArrayList<SkillsDataClass>()
        val query = "SELECT * FROM $TABLE_SKILL WHERE $SKILL_Email = '$emailid' AND $SKILL_Source = '$source'"

        val db = this.readableDatabase
        val c = db.rawQuery(query, null)
        if (c != null) {
            while (c.moveToNext()) {

                val skill = SkillsDataClass()
                skill.sid = c.getInt(c.getColumnIndex(SKILL_ID))
                skill.personalemail = c.getString(c.getColumnIndex(SKILL_Email))
                skill.source = c.getString(c.getColumnIndex(SKILL_Source))
                skill.skills = c.getString(c.getColumnIndex(SKILL_skills))
                skilldetails.add(skill)
            }
        }
        c!!.close()
        return skilldetails
    }

    fun allExperienceDetails(emailid: String, source: String): List<ExperienceDataClass> {
        val experiencedetails = ArrayList<ExperienceDataClass>()
        val query = "SELECT * FROM $TABLE_EXPERIENCE WHERE $EXPERIENCE_Email = '$emailid' AND $EXPERIENCE_Source = '$source' ORDER BY (substr($EXPERIENCE_Enddate, 4, 7)) "

        val db = this.readableDatabase
        val c = db.rawQuery(query, null)
        if (c != null) {
            while (c.moveToNext()) {

                val experience = ExperienceDataClass()
                experience.eid = c.getInt(c.getColumnIndex(EXPERIENCE_ID))
                experience.personalemail = c.getString(c.getColumnIndex(EXPERIENCE_Email))
                experience.source = c.getString(c.getColumnIndex(EXPERIENCE_Source))
                experience.company = c.getString(c.getColumnIndex(EXPERIENCE_Company))
                experience.title = c.getString(c.getColumnIndex(EXPERIENCE_Title))
                experience.location = c.getString(c.getColumnIndex(EXPERIENCE_Location))
                experience.startdate = c.getString(c.getColumnIndex(EXPERIENCE_Startdate))
                experience.enddate = c.getString(c.getColumnIndex(EXPERIENCE_Enddate))
                experience.description = c.getString(c.getColumnIndex(EXPERIENCE_Description))
                experience.currentlyworkingornot = c.getString(c.getColumnIndex(EXPERIENCE_Currentlyworkingornot))

                experiencedetails.add(experience)
            }
        }
        c!!.close()
        return experiencedetails
    }
    fun addContact(ContactNumber: String, ContactName: String,ContactEmail: String) {
        val values = ContentValues()
        values.put(CONTACT_UserName, ContactName)
        values.put(CONTACT_NUMBER, ContactNumber)
        values.put(CONTACT_EmailID, ContactEmail)
        val db = writableDatabase
        db.insert(TABLE_CONTACT, null, values)
        db.close()
    }
    fun addContactdata(contactdata: ContactListItem) : Long{

        val db = this.writableDatabase
        val values = ContentValues()
        values.put(CONTACT_UserName, contactdata.contactusername)
        values.put(CONTACT_NUMBER, contactdata.contactNumber)
        values.put(CONTACT_EmailID, contactdata.contactemail)
        values.put(CONTACT_Firstname, contactdata.contactfirstname)
        values.put(CONTACT_Lastname, contactdata.contactlastname)
        values.put(CONTACT_Birthdate, contactdata.contactebirthdate)
        values.put(CONTACT_ProfilePic, contactdata.contactimage)
        values.put(CONTACT_Address, contactdata.contactaddress)
        values.put(CONTACT_AdditionalNumber, contactdata.contactadditionalnumber)
        values.put(CONTACT_Gender, contactdata.contactgender)
        values.put(CONTACT_Tage, contactdata.contacttage)
        values.put(CONTACT_Countrycode, contactdata.contactcountrycode)
        values.put(CONTACT_Status, contactdata.status)
        return db.insert(TABLE_CONTACT, null, values)

    }


    fun updateContactdata(contactdata: ContactListItem) {


        val db = this.writableDatabase
        val values = ContentValues()
        values.put(CONTACT_UserName, contactdata.contactusername)
        values.put(CONTACT_NUMBER, contactdata.contactNumber)
        values.put(CONTACT_EmailID, contactdata.contactemail)
        values.put(CONTACT_Firstname, contactdata.contactfirstname)
        values.put(CONTACT_Lastname, contactdata.contactlastname)
        values.put(CONTACT_Birthdate, contactdata.contactebirthdate)
        values.put(CONTACT_ProfilePic, contactdata.contactimage)
        values.put(CONTACT_Address, contactdata.contactaddress)
        values.put(CONTACT_AdditionalNumber, contactdata.contactadditionalnumber)
        values.put(CONTACT_Gender, contactdata.contactgender)
        values.put(CONTACT_Tage, contactdata.contacttage)
        values.put(CONTACT_Countrycode, contactdata.contactcountrycode)
        values.put(CONTACT_Status, contactdata.status)

        db.update(TABLE_CONTACT, values, ""+ CONTACT_ID +"= '"+ contactdata.contactid+"' ", null)
        db.close()
    }

    fun addDublicateContactdata(contactdata: ContactListItem) : Long{

        val db = this.writableDatabase
        val values = ContentValues()
        values.put(DUPLICATE_UserName, contactdata.contactusername)
        values.put(DUPLICATE_NUMBER, contactdata.contactNumber)
        values.put(DUPLICATE_EmailID, contactdata.contactemail)
        values.put(DUPLICATE_Firstname, contactdata.contactfirstname)
        values.put(DUPLICATE_Lastname, contactdata.contactlastname)
        values.put(DUPLICATE_Birthdate, contactdata.contactebirthdate)
        values.put(DUPLICATE_ProfilePic, contactdata.contactimage)
        values.put(DUPLICATE_Address, contactdata.contactaddress)
        values.put(DUPLICATE_AdditionalNumber, contactdata.contactadditionalnumber)
        values.put(DUPLICATE_Gender, contactdata.contactgender)
        values.put(DUPLICATE_Tage, contactdata.contacttage)
        values.put(DUPLICATE_Countrycode, contactdata.contactcountrycode)
        values.put(DUPLICATE_Status, contactdata.status)
        return db.insert(TABLE_DUBLICATECONTACT, null, values)

    }




    fun updateDublicateContactdata(contactdata: ContactListItem) {


        val db = this.writableDatabase
        val values = ContentValues()
        values.put(DUPLICATE_UserName, contactdata.contactusername)
        values.put(DUPLICATE_NUMBER, contactdata.contactNumber)
        values.put(DUPLICATE_EmailID, contactdata.contactemail)
        values.put(DUPLICATE_Firstname, contactdata.contactfirstname)
        values.put(DUPLICATE_Lastname, contactdata.contactlastname)
        values.put(DUPLICATE_Birthdate, contactdata.contactebirthdate)
        values.put(DUPLICATE_ProfilePic, contactdata.contactimage)
        values.put(DUPLICATE_Address, contactdata.contactaddress)
        values.put(DUPLICATE_AdditionalNumber, contactdata.contactadditionalnumber)
        values.put(DUPLICATE_Gender, contactdata.contactgender)
        values.put(DUPLICATE_Tage, contactdata.contacttage)
        values.put(DUPLICATE_Countrycode, contactdata.contactcountrycode)
        values.put(DUPLICATE_Status, contactdata.status)


        db.update(TABLE_DUBLICATECONTACT, values, ""+ DUPLICATE_ID+"= '"+ contactdata.contactid+"' ", null)
        db.close()
    }


    fun addBlockContactdata(contactdata: ContactListItem) : Long{

        val db = this.writableDatabase
        val values = ContentValues()
        values.put(BLOCK_UserName, contactdata.contactusername)
        values.put(BLOCK_NUMBER, contactdata.contactNumber)
        values.put(BLOCK_EmailID, contactdata.contactemail)
        values.put(BLOCK_Firstname, contactdata.contactfirstname)
        values.put(BLOCK_Lastname, contactdata.contactlastname)
        values.put(BLOCK_Birthdate, contactdata.contactebirthdate)
        values.put(BLOCK_ProfilePic, contactdata.contactimage)
        values.put(BLOCK_Address, contactdata.contactaddress)
        values.put(BLOCK_AdditionalNumber, contactdata.contactadditionalnumber)
        values.put(BLOCK_Gender, contactdata.contactgender)
        values.put(BLOCK_Tage, contactdata.contacttage)
        values.put(BLOCK_Countrycode, contactdata.contactcountrycode)
        values.put(BLOCK_Status, contactdata.status)
        return db.insert(TABLE_BLOCKCONTACT, null, values)

    }



    fun addregisterContactdata(contactdata: UserRegistrationClass) : Long{

        val db = this.writableDatabase
        val values = ContentValues()
        values.put(Register_UserName, contactdata.username)
        values.put(Register_NUMBER, contactdata.mobilenumber)
        values.put(Register_EmailID, contactdata.personalemail)
        values.put(Register_Firstname, contactdata.firstname)
        values.put(Register_Lastname, contactdata.lastname)
        values.put(Register_Birthdate, contactdata.birthdate)
        values.put(Register_ProfilePic, contactdata.profilepic)
        values.put(Register_Address, contactdata.address)
        values.put(Register_AdditionalNumber, contactdata.additionalnumber)
        values.put(Register_Gender, contactdata.gender)
        values.put(Register_Tage, contactdata.tagename)
        values.put(Register_Countrycode, contactdata.countrycode)
        return db.insert(TABLE_RegisterCONTACT, null, values)

    }



    fun updateregisterContactdata(contactdata: UserRegistrationClass) {


        val db = this.writableDatabase
        val values = ContentValues()
        values.put(Register_UserName, contactdata.username)
        values.put(Register_NUMBER, contactdata.mobilenumber)
        values.put(Register_EmailID, contactdata.personalemail)
        values.put(Register_Firstname, contactdata.firstname)
        values.put(Register_Lastname, contactdata.lastname)
        values.put(Register_Birthdate, contactdata.birthdate)
        values.put(Register_ProfilePic, contactdata.profilepic)
        values.put(Register_Address, contactdata.address)
        values.put(Register_AdditionalNumber, contactdata.additionalnumber)
        values.put(Register_Gender, contactdata.gender)
        values.put(Register_Tage, contactdata.tagename)
        values.put(Register_Countrycode, contactdata.countrycode)

        db.update(TABLE_RegisterCONTACT, values, ""+ Register_ID+"= '"+ contactdata.registerid+"' ", null)
        db.close()
    }

    fun getDublicateAllData(): Cursor {
        val db = this.writableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_DUBLICATECONTACT ORDER BY $DUPLICATE_UserName", null)
    }

    fun getCheckdataData(mobilenumber:String): Cursor {
        val db = this.writableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_DUBLICATECONTACT WHERE $DUPLICATE_NUMBER = '$mobilenumber'", null)
    }

    fun getRegisterData(username:String,mobilenumber:String): Cursor {
        val db = this.writableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_RegisterCONTACT WHERE $Register_NUMBER = '$username' OR $Register_NUMBER = '$mobilenumber' ORDER BY $Register_UserName", null)
    }

    fun allgetRegisterData(): Cursor {
        val db = this.writableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_RegisterCONTACT ORDER BY $Register_UserName", null)
    }

    fun deletecontact(id: Int) {

        val db = writableDatabase
        db.delete(TABLE_CONTACT, "$CONTACT_ID = ? ", arrayOf(id.toString()))
    }
    fun getAllData(): Cursor {
        val db = this.writableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_CONTACT ORDER BY $CONTACT_UserName", null)
    }
    fun deleteblockcontact(id: Int) {

        val db = writableDatabase
        db.delete(TABLE_BLOCKCONTACT, "$BLOCK_ID = ? ", arrayOf(id.toString()))
    }
    fun getBlockContactData(): Cursor {
        val db = this.writableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_BLOCKCONTACT ORDER BY $BLOCK_UserName", null)
    }
    fun getBlockContactData(mobilenumber:String): Cursor {
        val db = this.writableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_BLOCKCONTACT WHERE $BLOCK_NUMBER = '$mobilenumber'", null)
    }
    fun tableExists(): Int {
        val db = this.writableDatabase
        val cursor = db.rawQuery("SELECT  * FROM $TABLE_CONTACT", null)
        val count = cursor.count
        cursor.close()
        return count
    }
}
