package fr.xebia.xoverflow.model;

import static org.apache.commons.lang.StringUtils.isBlank;

public class User extends UserLight {

    private final String email;

    private final String avatarUrl;

    public User(String id, String username, String email, String avatarUrl) {
        super(id, username);
        if (isBlank(email)) {
            throw new IllegalArgumentException("email must be defined.");
        }
        this.email = email;
        this.avatarUrl = avatarUrl;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public static User.Builder builderUser() {
        return new Builder();
    }

    public static User.Builder builder(User userToCopy) {
        return new Builder(userToCopy);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        User user = (User) o;

        if (!email.equals(user.email)) return false;
        return avatarUrl.equals(user.avatarUrl);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + avatarUrl.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }

    public static class Builder  implements IdBuilder<User> {

        protected String id;

        protected String username;

        private String email;

        private String avatarUrl;

        private Builder() {
            //
        }

        private Builder(User userLightToCopy) {
            id = userLightToCopy.getId();
            username = userLightToCopy.getUsername();
            email = userLightToCopy.getEmail();
            avatarUrl = userLightToCopy.getAvatarUrl();
        }

        @Override
        public User build() {
            return new User(id, username, email, avatarUrl);
        }

        public void setId(String id) {
            this.id = id;
        }

        public User.Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }
    }
}
