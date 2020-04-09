import React from "react";
import {
  Button,
  TextField,
  Container,
  Grid,
  Typography,
  Avatar,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
} from "@material-ui/core";
import { makeStyles } from '@material-ui/core/styles';
import AccountBoxIcon from "@material-ui/icons/AccountBox";
import LocalHospitalIcon from "@material-ui/icons/LocalHospital";
import { Link as RouterLink } from "react-router-dom";
import AccountAPI from "../../mocks/test_users.js";


const useStyles = makeStyles({
  button: {
    background: '#00B5AD',
    border: 0,
    color: 'white',
    height: 48,
    marginTop: '1rem',
    '&:hover': {
      background: '#00B5AD',
    }
  },

  mainHeader: {
    marginTop: "10rem",
    color: "#00B5AD"
  },

  secondHeader : {
    margin: "5rem 0 1rem 0"
  },

  version: {
    marginTop: '2rem',
    fontSize: "16px",
    color: "#00B5AD"
  }
});

function ListItemLink(props) {
  const { primary, to } = props;

  const renderLink = React.useMemo(
    () =>
      React.forwardRef((itemProps, ref) => (
        <RouterLink to={to} ref={ref} {...itemProps} />
      )),
    [to]
  );

  return (
    <li>
      <ListItem button component={renderLink}>
        <ListItemIcon>
          {props.icon === "Doctor" ? <LocalHospitalIcon /> : <AccountBoxIcon />}
        </ListItemIcon>
        <ListItemText primary={primary} />
      </ListItem>
    </li>
  );
}

const Login = () => {
  const classes = useStyles();
  return (
    <Container component="main" maxWidth="sm">
      <Grid container className={classes.mainHeader}>
        <Typography variant="h4"  align="center">
          Welcome to Telemedicine Demo &nbsp;&nbsp;
        </Typography>
        <Avatar src="./telemed-logo.png" />
      </Grid>
      <Grid container justify="center" spacing={10}>
        <Grid item xs={6}>
          <Typography variant="h6" className={classes.secondHeader}>Sign In</Typography>
          <form>
            <TextField
              variant="outlined"
              margin="normal"
              required
              fullWidth
              id="email"
              label="Email Address"
              name="email"
            />
            <TextField
              variant="outlined"
              margin="normal"
              required
              fullWidth
              name="password"
              label="Password"
              type="password"
              id="password"
            />

            <Button
            className={classes.button}
              type="submit"
              fullWidth
              size="large"
              variant="contained"
              color="primary"
            >
              Login
            </Button>
          </form>
        </Grid>
        <Grid item xs={6}>
          <Typography variant="h6"  className={classes.secondHeader}>Or use demo account</Typography>
          <List>


            {AccountAPI.all().map((user) => (
              /**  @TODO use test accounts from back app **/
              // store into dom element a login and pswd of the test user to use for log in
              <ListItemLink
                to={calcPath2Board(user)}
                primary={user.user_type + " " + user.name}
                icon={user.user_type}
              />
              //set OnClick funtion as submission of login form with creds of test user
            ))}
          </List>
        </Grid>
      </Grid>

      <Typography variant="body1" className={classes.version} align="center">
        v{process.env.REACT_APP_DEMO_VERSION}
      </Typography>
    </Container>
  );
};

function calcPath2Board(user) {
  let path = user.user_type === "Doctor" ? "/doctor/" : "/user/";
  path += user.id + "/board";
  console.log("test user:", user, ";  PATH= ", path);
  return path;
}

export default Login;