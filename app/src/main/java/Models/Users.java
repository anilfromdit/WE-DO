package Models;

public class Users {
    String name,profilePic,username,Isver,gender,bio,Email,Dob,Id,status;

    public Users(String name, String profilePic,String Id, String username,String Isver,String gender,String Bio,String Email,String Dob,String status) {
        this.name = name;
        this.profilePic = profilePic;
        this.username = username;
        this.Isver=Isver;
        this.Dob = Dob;
        this.gender=gender;
        this.bio=Bio;
        this.Email=Email;
        this.Id=Id;
        this.status=status;
    }

    public Users() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getDob() {
        return Dob;
    }

    public void setDob(String dob) {
        Dob = dob;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public String getIsver() {
        return Isver;
    }

    public void setIsver(String Isver) {
        this.Isver = Isver;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
