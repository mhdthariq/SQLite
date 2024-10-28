package com.thariq.sqlite;

import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class DashboardActivity extends AppCompatActivity {

    private EditText studentName, studentCourse;
    private Button addStudentButton, updateStudentButton, deleteStudentButton;
    private ListView studentListView;
    private DatabaseHelper db;
    private int selectedStudentId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initializeViews();
        db = new DatabaseHelper(this);
        loadStudentData();

        setUpButtonListeners();
    }

    private void initializeViews() {
        studentName = findViewById(R.id.studentName);
        studentCourse = findViewById(R.id.studentCourse);
        addStudentButton = findViewById(R.id.addStudentButton);
        updateStudentButton = findViewById(R.id.updateStudentButton);
        deleteStudentButton = findViewById(R.id.deleteStudentButton);
        studentListView = findViewById(R.id.studentListView);
    }

    private void setUpButtonListeners() {
        addStudentButton.setOnClickListener(v -> addStudent());
        updateStudentButton.setOnClickListener(v -> updateStudent());
        deleteStudentButton.setOnClickListener(v -> deleteStudent());
        studentListView.setOnItemClickListener((parent, view, position, id) -> selectStudent(position));
    }

    private void addStudent() {
        String name = studentName.getText().toString().trim();
        String course = studentCourse.getText().toString().trim();

        if (name.isEmpty() || course.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (db.insertStudent(name, course)) {
            Toast.makeText(this, "Student Added", Toast.LENGTH_SHORT).show();
            loadStudentData();
            clearInputFields();
        } else {
            Toast.makeText(this, "Error Adding Student", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateStudent() {
        String name = studentName.getText().toString().trim();
        String course = studentCourse.getText().toString().trim();

        if (selectedStudentId == -1) {
            Toast.makeText(this, "Please select a student to update", Toast.LENGTH_SHORT).show();
            return;
        }

        if (db.updateStudent(selectedStudentId, name, course)) {
            Toast.makeText(this, "Student Updated", Toast.LENGTH_SHORT).show();
            loadStudentData();
            clearInputFields();
        } else {
            Toast.makeText(this, "Error Updating Student", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteStudent() {
        if (selectedStudentId == -1) {
            Toast.makeText(this, "Please select a student to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        if (db.deleteStudent(selectedStudentId) > 0) {
            Toast.makeText(this, "Student Deleted", Toast.LENGTH_SHORT).show();
            loadStudentData();
            clearInputFields();
        } else {
            Toast.makeText(this, "Error Deleting Student", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectStudent(int position) {
        Cursor cursor = (Cursor) studentListView.getItemAtPosition(position);
        if (cursor != null) {
            // Change "id" to "_id"
            selectedStudentId = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
            studentName.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            studentCourse.setText(cursor.getString(cursor.getColumnIndexOrThrow("course")));
        }
    }

    private void loadStudentData() {
        Cursor cursor = db.getAllStudents();
        String[] from = new String[]{"_id", "name", "course"};  // Ensure "_id" is included
        int[] to = new int[]{R.id.studentNameView, R.id.studentCourseView};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.student_list_item, cursor, from, to, 0);
        studentListView.setAdapter(adapter);
    }

    private void clearInputFields() {
        studentName.setText("");
        studentCourse.setText("");
        selectedStudentId = -1;
    }
}
