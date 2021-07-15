import { Navbar, Nav } from 'react-bootstrap';
import { AmplifySignOut } from '@aws-amplify/ui-react';
import './Navigation.css'

const Navigation = () => {
    return (
        <Navbar bg="dark" variant='dark' expand="lg">
            <Navbar.Brand href="/">Makaniti?!</Navbar.Brand>
            <Navbar.Toggle aria-controls="responsive-navbar-nav" />
            <Navbar.Collapse id="responsive-navbar-nav">
                <Nav className="mr-auto reg-nav-btn">
                    <Nav.Link href='/'>Home</Nav.Link>
                </Nav>
                <Nav className="mr-auto sign-out-btn">
                    <Nav.Link>
                        <AmplifySignOut />
                    </Nav.Link>
                </Nav>
            </Navbar.Collapse >
        </Navbar >
    )
}

export default Navigation