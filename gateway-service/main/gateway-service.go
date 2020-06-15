package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"log"
	"net"
	"net/http"
	"strconv"

	mux "github.com/gorilla/mux"
	uuid "github.com/satori/go.uuid"
)

func main() {
	registerService()
	fmt.Println("Registered service")
	registerNode()
	fmt.Println("Registered Node")
	go respondToHealthChecks()
	fmt.Println("Set health checks")
	respondToExternalRequests()
}

func registerService() {
	values := make(map[string]interface{})
	values["serviceName"] = "gateway"
	values["methodNames"] = [1]string{"blacklist"}
	jsonValue, _ := json.Marshal(values)
	resp, err := http.Post("http://172.31.53.254:1234/service/register", "application/json", bytes.NewBuffer(jsonValue))
	//resp, err := http.Post("http://localhost:1234/service/register", "application/json", bytes.NewBuffer(jsonValue))
	if err != nil {
		panic(err)
	}
	defer resp.Body.Close()
	if resp.StatusCode == http.StatusOK {
		bodyBytes, _ := ioutil.ReadAll(resp.Body)
		bodyString := string(bodyBytes)
		fmt.Println(bodyString)
	}
}

func registerNode() {
	identifier, _ := uuid.NewV4()
	outboundIP := GetOutboundIP().String()
	fmt.Println(outboundIP)
	values := map[string]interface{}{"id": identifier.String(), "ipAddress": outboundIP, "serviceName": "gateway", "port": 5000}
	jsonValue, _ := json.Marshal(values)
	req, err := http.NewRequest("PUT", "http://172.31.53.254:1234/node", bytes.NewBuffer(jsonValue))
	//req, err := http.NewRequest("PUT", "http://localhost:1234/node", bytes.NewBuffer(jsonValue))
	if err != nil {
		return
	}
	req.Header.Set("Content-Type", "application/json")
	resp, err := http.DefaultClient.Do(req)
	if err != nil {
		panic(err)
	}
	defer resp.Body.Close()
	if resp.StatusCode == http.StatusOK {
		bodyBytes, _ := ioutil.ReadAll(resp.Body)
		bodyString := string(bodyBytes)
		fmt.Println(bodyString)
	}
}

func respondToHealthChecks() {
	router := mux.NewRouter().StrictSlash(true)
	router.HandleFunc("/health", blank)
	log.Fatal(http.ListenAndServe(":5000", router))
}

func blank(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, "")
}

func respondToExternalRequests() {
	router := mux.NewRouter().StrictSlash(true)
	router.HandleFunc("/{methodName}", route)
	log.Fatal(http.ListenAndServe(":5002", router))
}

func route(w http.ResponseWriter, r *http.Request) {
	vars := mux.Vars(r)
	url := "http://172.31.53.254:1234/getHandlers?methodName=" + vars["methodName"]
	//url := "http://localhost:1234/getHandlers?methodName=" + vars["methodName"]
	fmt.Println(r.Method)
	fmt.Println(url)
	resp, err := http.Get(url)
	if err != nil {
		panic(err)
	}
	defer resp.Body.Close()
	if resp.StatusCode == http.StatusOK {
		var nodes []Node
		json.NewDecoder(resp.Body).Decode(&nodes)
		node := nodes[0]
		fmt.Println(nodes)
		redirectURL := "http://" + node.IPAddress + ":" + strconv.Itoa(node.Port) + "/" + vars["methodName"] + "?" + r.URL.RawQuery
		fmt.Println(redirectURL)
		redirectRequest, err := http.NewRequest(r.Method, redirectURL, r.Body)
		if err != nil {
			return
		}
		redirectRequest.Header.Set("Content-Type", "application/json")
		redirectResponse, err := http.DefaultClient.Do(redirectRequest)
		if err != nil {
			panic(err)
		}
		defer redirectResponse.Body.Close()
		if redirectResponse.StatusCode == http.StatusOK {
			bodyBytes, _ := ioutil.ReadAll(redirectResponse.Body)
			bodyString := string(bodyBytes)
			fmt.Println(bodyString)
			fmt.Fprintf(w, bodyString)
		}
	}
}

//Node is a service node in the system
type Node struct {
	ID          string `json:"id"`
	IPAddress   string `json:"ipAddress"`
	ServiceName string `json:"serviceName"`
	Port        int    `json:"port"`
}

func (n *Node) String() string {
	return n.IPAddress + " " + n.ServiceName + " " + n.ID
}

//GetOutboundIP Get preferred outbound ip of this machine
func GetOutboundIP() net.IP {
	conn, err := net.Dial("udp", "8.8.8.8:80")
	if err != nil {
		log.Fatal(err)
	}
	defer conn.Close()

	localAddr := conn.LocalAddr().(*net.UDPAddr)

	return localAddr.IP
}
