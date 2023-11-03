package service.core;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Data Class that contains client information
 * 
 * @author Rem
 *
 */
public class ClientInfo implements Serializable {

	// Add a version ID for serialization.
    private static final long serialVersionUID = 1L;
	
	public static final char MALE				= 'M';
	public static final char FEMALE				= 'F';
	
	public ClientInfo(String name, char sex, int age, double height, double weight, boolean smoker, boolean medicalIssues) {
		this.name = name;
		this.gender = sex;
		this.age = age;
		this.height = height;
		this.weight = weight;
		this.smoker = smoker;
		this.medicalIssues = medicalIssues;
	}
	
	public ClientInfo() {}

    /**
     * Public fields are used as modern best practice argues that use of set/get
     * methods is unnecessary as (1) set/get makes the field mutable anyway, and
     * (2) set/get introduces additional method calls, which reduces performance.
     */
    public String name;
    public char gender;
    public int age;
    public double height;
    public double weight;
    public boolean smoker;
    public boolean medicalIssues;

    /**
     * Debugging method to check if object is being serialized correctly.
     *
     * @param out the ObjectOutputStream
     * @throws IOException if an I/O error occurs
     */
    // private void writeObject(ObjectOutputStream out) throws IOException {
    //     System.out.println("Writing Object: " + this);
    //     out.defaultWriteObject();
    // }

    /**
     * Debugging method to check if object is being deserialized correctly.
     *
     * @param in the ObjectInputStream
     * @throws IOException if an I/O error occurs
     * @throws ClassNotFoundException if the class of a serialized object could not be found
     */
    // private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    //     System.out.println("Reading Object");
    //     in.defaultReadObject();
    // }

    /**
     * Override the toString method for better debug output.
     * @return the string representation of the object
     */
    @Override
    public String toString() {
        return "ClientInfo{" +
                "name='" + name + '\'' +
                ", gender=" + gender +
                ", age=" + age +
                ", height=" + height +
                ", weight=" + weight +
                ", smoker=" + smoker +
                ", medicalIssues=" + medicalIssues +
                '}';
    }
}